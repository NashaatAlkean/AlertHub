package org.loader.service.parser;

import org.loader.model.PlatformInformation;
import org.loader.model.enums.Provider;

import java.io.File;
import java.util.List;

/**
 * Interface for parsing provider data files.
 * Each provider (GitHub, Jira, ClickUp) has its own implementation.
 */
public interface DataParser {

    /**
     * Parse a data file and convert it to PlatformInformation entities.
     *
     * @param file the file to parse
     * @return list of parsed PlatformInformation entities
     * @throws com.alerthub.loader.exception.FileParsingException if parsing fails
     */
    List<PlatformInformation> parse(File file);

    /**
     * Get the provider this parser handles.
     *
     * @return the provider type
     */
    Provider getProvider();
}