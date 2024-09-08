package com.siyu.server.dto;

import com.siyu.common.domain.dto.VisitorInfoDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LinkLogMessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String shortUrl;

    private VisitorInfoDTO visitorInfoDTO;
}
