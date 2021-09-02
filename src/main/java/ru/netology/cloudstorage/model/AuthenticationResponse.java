package ru.netology.cloudstorage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class AuthenticationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -1608956189232399152L;

    @JsonProperty("auth-token")
    private final String jwt;
}
