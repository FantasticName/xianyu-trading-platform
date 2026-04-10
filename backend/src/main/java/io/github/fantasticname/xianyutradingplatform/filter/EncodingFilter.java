package io.github.fantasticname.xianyutradingplatform.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * @author FantasticName
 */
/**
 * 编码过滤器：统一设置请求与响应的字符编码为 UTF-8，避免中文乱码。
 *
 * <p>由于项目直接读取请求体/写出 JSON，字符编码需要在尽早阶段设定；
 * 该过滤器通常放在链路前端，使后续读取 body 与写出响应都按 UTF-8 处理。</p>
 *
 * @author FantasticName
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1) 设置请求编码，影响参数解析与读取输入流时的默认字符集。
        request.setCharacterEncoding("UTF-8");
        // 2) 设置响应编码，确保响应体（如 JSON）以 UTF-8 写出。
        response.setCharacterEncoding("UTF-8");
        // 3) 继续过滤器链。
        chain.doFilter(request, response);
    }
}

