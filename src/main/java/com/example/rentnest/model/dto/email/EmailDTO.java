package com.example.rentnest.model.dto.email;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailDTO {
    private List<String> emailToList;
    private List<String> emailCCList;
    private List<String> emailBCCList;
    private String subject;
    private String body;
}
