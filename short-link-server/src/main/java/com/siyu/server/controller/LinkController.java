package com.siyu.server.controller;

import com.siyu.common.constants.RequestHeaderConstants;
import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.server.annotation.VisitorOperationLogger;
import com.siyu.server.entity.Link;
import com.siyu.server.enums.VisitorOperationType;
import com.siyu.server.service.LinkService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
public class LinkController {

    private final static String ENTITY = "link";

    private final LinkService linkService;

    @VisitorOperationLogger(operationType = VisitorOperationType.CREATE_LINK, operationTarget = ENTITY)
    @PostMapping
    public R<Link> create(@NotEmpty @RequestParam String originalUrl) {
        return R.ok(linkService.create(originalUrl));
    }

    @VisitorOperationLogger(operationType = VisitorOperationType.ACCESS_LINK, operationTarget = ENTITY)
    @GetMapping("/{shortUriSuffix}")
    public R<?> redirect(@NotEmpty @PathVariable String shortUriSuffix) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        HttpServletResponse response = Objects.requireNonNull(requestAttributes.getResponse());

        return linkService.redirect(shortUriSuffix, response) ? R.noContent() : R.fail(ErrorStatus.NOT_FOUND);

    }
}
