package org.mbarek0.folioflex.web.vm.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenVM {
    private String token;
    private String refreshToken;
}
