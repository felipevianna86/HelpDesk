package br.com.helpdesk.api.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * @author felipe
 *
 *	Classe para manipulação do Token.
 */
@Component
public class JWTTokenUtil implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3090099878847748123L;
	
	
	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "created";
	static final String CLAIM_KEY_EXPIRED = "exp";
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private Long expiration;

	/**
	 * Método para obter o e-mail que está dentro do Token.
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token) {
		String username;
		
		try {
			final Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		
		return username;
	}
	
	/**
	 * Retorna a data de expiração de um Token JWT
	 *
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		
		try {
			final Claims claims = getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		
		return expiration;
	}
	
	/**
	 * Faz o 'Parse' do Token JWT pra que as informações possam ser extraídas.
	 * @param token
	 * @return
	 */
	private Claims getClaimsFromToken(String token) {
		Claims claims;
		
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		
		return claims;
		
	}
	
	/**
	 * Verifica se o token está expirado.
	 * @param token
	 * @return
	 */
	private Boolean isTokenExpired(String token) {
 		return getExpirationDateFromToken(token).before(new Date());
	}
	
	/**
	 * Método para gerar o Token.
	 * @param userDetails
	 * @return
	 */
	public String generateToken(UserDetails userDetails) {
		
		Map<String, Object> claims = new HashMap<>();
		
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAIM_KEY_CREATED, new Date());
		
		return doGenerateToken(claims);
	}
	
	/**
	 * Método auxiliar para geração do Token.
	 * @param claims
	 * @return
	 */
	private String doGenerateToken(Map<String, Object> claims) {
		
		final Date createdDate = (Date) claims.get(CLAIM_KEY_CREATED);
		final Date expirationDate = new Date(createdDate.getTime() + expiration * 1000);
		
		return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	/**
	 * Verifica se o token pode ser atualizado.
	 * @param token
	 * @return
	 */
	public Boolean canTokenBeRefreshed(String token) {
		
		return !isTokenExpired(token);
	}
	
	/**
	 * Classe que atualiza o token.
	 * @param token
	 * @return
	 */
	public String refreshToken(String token) {
		
		String refreshedToken;
		
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, new Date());
			refreshedToken = doGenerateToken(claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		
		return refreshedToken;
	}
	
	/**
	 * Método que realiza a verificação se o token ainda é válido.
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		JWTUser user = (JWTUser) userDetails;
		
		final String username = getUsernameFromToken(token);
		
		return username.equals(user.getUsername()) && !isTokenExpired(token);
	}
}
