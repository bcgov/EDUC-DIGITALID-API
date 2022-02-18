ALTER TABLE DIGITAL_IDENTITY
    ADD (
        AUTO_MATCHED VARCHAR2(1) DEFAULT 'N' NOT NULL
        );
COMMENT ON COLUMN DIGITAL_IDENTITY.AUTO_MATCHED IS 'This column is used to indicate if the digital identity has been auto matched';
