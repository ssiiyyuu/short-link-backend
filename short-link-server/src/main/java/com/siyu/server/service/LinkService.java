package com.siyu.server.service;

import com.siyu.server.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-09-07 12:47:58
 */
public interface LinkService extends IService<Link> {

    Link create(String originalUrl);

    boolean redirect(String shortUriSuffix, HttpServletResponse response);
}
