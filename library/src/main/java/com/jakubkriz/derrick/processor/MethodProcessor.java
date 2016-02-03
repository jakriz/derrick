package com.jakubkriz.derrick.processor;

import com.jakubkriz.derrick.annotation.SourceFrom;
import com.jakubkriz.derrick.downloader.CodeDownloader;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.lang.model.element.ExecutableElement;
import java.io.IOException;

public class MethodProcessor {

    private CodeDownloader codeDownloader;

    public MethodProcessor(CodeDownloader codeDownloader) {
        this.codeDownloader = codeDownloader;
    }

    public MethodDefinition process(ExecutableElement element) throws IOException {
        MethodDefinition methodDefinition = new MethodDefinition();
        methodDefinition.setName(element.getSimpleName().toString());

        SourceFrom annotation = element.getAnnotation(SourceFrom.class);

        String url = annotation.url();
        String selector = annotation.selector();

        String encodedCode = codeDownloader.getMethodCode(url, selector);
        String code = StringEscapeUtils.unescapeHtml4(encodedCode);

        methodDefinition.setCode(code);

        return methodDefinition;
    }
}
