package com.iroshnk.nftraffle.config.security;

import com.iroshnk.nftraffle.entity.*;
import com.iroshnk.nftraffle.repository.EntitlementHasRoleRepository;
import com.iroshnk.nftraffle.repository.GroupHasEntitlementRepository;
import com.iroshnk.nftraffle.repository.UserHasGroupRepository;
import com.iroshnk.nftraffle.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenHandler implements Serializable {

    private final UserService userService;

    private final UserHasGroupRepository userHasGroupRepository;

    private final GroupHasEntitlementRepository groupHasEntitlementRepository;

    private final EntitlementHasRoleRepository entitlementHasRoleRepository;

    @Value("${jwt.token-validity}")
    public long tokenValidity;

    @Value("${jwt.signing-key}")
    public String signingKey;

    @Value("${jwt.authorities-key}")
    public String authoritiesKey;

    public JwtTokenHandler(UserService userService,
                           UserHasGroupRepository userHasGroupRepository,
                           GroupHasEntitlementRepository groupHasEntitlementRepository,
                           EntitlementHasRoleRepository entitlementHasRoleRepository) {
        this.userService = userService;
        this.userHasGroupRepository = userHasGroupRepository;
        this.groupHasEntitlementRepository = groupHasEntitlementRepository;
        this.entitlementHasRoleRepository = entitlementHasRoleRepository;
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(authoritiesKey, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenFromUsername(String userName) {
        var user = userService.getUserByUsername(userName);
        var userGroups = userHasGroupRepository.findUserHasGroupByUserUserId(user.get().getUserId());
        List<Long> groupIds = userGroups.stream()
                .map(UserHasGroup::getGroup)
                .map(Group::getGroupId)
                .collect(Collectors.toList());

        var groupEntitlements = groupHasEntitlementRepository.getGroupHasEntitlementByGroupGroupIdIn(groupIds);
        List<Long> entitlementIds = groupEntitlements.stream()
                .map(GroupHasEntitlement::getEntitlement)
                .map(Entitlement::getEntitlementId)
                .collect(Collectors.toList());

        var entitlementRoles = entitlementHasRoleRepository.findEntitlementHasRolesByEntitlementEntitlementIdIn(entitlementIds);
        String authorities = entitlementRoles.stream()
                .map(EntitlementHasRole::getRole)
                .map(Role::getRoleName)
                .distinct()
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(user.get().getEmail())
                .claim(authoritiesKey, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && userService.userSessionHasToken(username, token));
    }

    UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key()).build();

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(authoritiesKey).toString().split(","))
                        .map(RolePrefixGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(signingKey));
    }
}
