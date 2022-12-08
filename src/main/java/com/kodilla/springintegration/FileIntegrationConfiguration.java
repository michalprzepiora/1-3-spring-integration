package com.kodilla.springintegration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;

@Configuration
public class FileIntegrationConfiguration {
    private static final String INPUT_DIRECTORY = "data/input";
    private static final String OUTPUT_DIRECTORY = "data/output";
    private static final String RESULT_FILE_NAME = "result.txt";
    private static final long TIME_MS_INTERVAL_CHECK_HOTFOLDER = 1000;

    @Bean
    IntegrationFlow fileIntegrationFlow(FileReadingMessageSource fileAdapter,
                                        FileNameReader fileNameReader,
                                        FileWritingMessageHandler outputFileHandler) {
        return IntegrationFlows.from(fileAdapter, config -> config.poller(Pollers.fixedDelay(TIME_MS_INTERVAL_CHECK_HOTFOLDER)))
                .transform(fileNameReader, "getFileName")
                 .handle(outputFileHandler)
                .get();
    }

    @Bean
    FileReadingMessageSource fileAdapter() {
        FileReadingMessageSource fileSource = new FileReadingMessageSource();
        fileSource.setDirectory(new File(INPUT_DIRECTORY));
        return fileSource;
    }

    @Bean
    FileNameReader getFileName() {
        return new FileNameReader();
    }

    @Bean
    FileWritingMessageHandler outputFileAdapter() {
        File directory = new File(OUTPUT_DIRECTORY);
        FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
        handler.setExpectReply(false);
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);
        handler.setFileNameGenerator(filename -> RESULT_FILE_NAME);
        return handler;
    }
}
