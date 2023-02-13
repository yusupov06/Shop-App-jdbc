package uz.md.shopappjdbc.dtos;

import lombok.*;

/**
 * Token DTO
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TokenDTO {

    /**
     * that is used to accessing user
     */
    private String accessToken;

    /**
     * that is used to refreshing access token
     */
    private String refreshToken;

    /**
     * token type
     */
    private final String tokenType = "Bearer ";
}
