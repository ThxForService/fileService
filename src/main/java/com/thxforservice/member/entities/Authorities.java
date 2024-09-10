package com.thxforservice.member.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import com.thxforservice.member.constants.Authority;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorities {

    private Authority authority;
}
