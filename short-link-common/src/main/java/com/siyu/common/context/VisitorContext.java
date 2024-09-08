package com.siyu.common.context;

import com.siyu.common.domain.dto.VisitorInfoDTO;

public class VisitorContext {

    private static final ThreadLocal<VisitorInfoDTO> threadLocal = new ThreadLocal<>();

    public static void setVisitorInfo(VisitorInfoDTO dto) {
        threadLocal.set(dto);
    }

    public static VisitorInfoDTO getVisitorInfo() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
