package org.loader.model.enums;

//Enum representing the processing status of a data file.

public enum ProcessStatus {
    //File was successfully processed and data was loaded into the database
    SUCCESS,

    //File processing failed due to an error
    FAILED,

    //File is currently being processed
    PROCESSING
}
