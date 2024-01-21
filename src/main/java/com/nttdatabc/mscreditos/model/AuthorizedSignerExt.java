package com.nttdatabc.mscreditos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizedSignerExt {
    private String dni;

    private String fullname;

    private String cargo;
}
