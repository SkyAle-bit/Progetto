package com.project.tesi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility per la gestione dei token JWT (JSON Web Token).
 *
 * Responsabilità:
 * <ul>
 *   <li>Generazione di un nuovo token a partire dai dati dell'utente autenticato</li>
 *   <li>Estrazione di claim dal token (email, scadenza, ecc.)</li>
 *   <li>Validazione del token (firma HMAC-SHA256 + scadenza)</li>
 * </ul>
 *
 * La chiave segreta e la durata di validità sono configurabili
 * tramite le proprietà {@code jwt.secret} e {@code jwt.expiration}.
 */
@Component
public class JwtUtil {

    /** Chiave segreta Base64 per la firma HMAC-SHA256 (da application.properties). */
    @Value("${jwt.secret}")
    private String secretKey;

    /** Durata di validità del token in millisecondi (da application.properties). */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Estrae l'email (subject) dal token JWT.
     *
     * @param token il token JWT
     * @return l'email dell'utente
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Estrae un claim generico dal token tramite una funzione di risoluzione.
     *
     * @param token          il token JWT
     * @param claimsResolver funzione che estrae il claim desiderato
     * @return il valore del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un nuovo token JWT per l'utente autenticato.
     * Il subject è l'email dell'utente.
     *
     * @param userDetails i dati dell'utente autenticato
     * @return il token JWT firmato
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifica se un token è valido: il subject deve corrispondere
     * all'username e il token non deve essere scaduto.
     *
     * @param token       il token JWT
     * @param userDetails i dati dell'utente da confrontare
     * @return {@code true} se il token è valido
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /** Verifica se il token è scaduto confrontando la data di scadenza con la data corrente. */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** Estrae la data di scadenza dal token. */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Decodifica e parsa tutti i claim contenuti nel token JWT. */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Decodifica la chiave segreta Base64 e restituisce la chiave HMAC per la firma. */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}