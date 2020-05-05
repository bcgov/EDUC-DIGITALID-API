CREATE TABLE DIGITAL_IDENTITY
(
    DIGITAL_IDENTITY_ID      RAW(16)              NOT NULL,
    STUDENT_ID               RAW(16),
    IDENTITY_TYPE_CODE       VARCHAR2(10)         NOT NULL,
    IDENTITY_VALUE           VARCHAR2(255)        NOT NULL,
    LAST_ACCESS_DATE         DATE                 NOT NULL,
    LAST_ACCESS_CHANNEL_CODE VARCHAR2(10)         NOT NULL,
    CREATE_USER              VARCHAR2(32)         NOT NULL,
    CREATE_DATE              DATE DEFAULT SYSDATE NOT NULL,
    UPDATE_USER              VARCHAR2(32)         NOT NULL,
    UPDATE_DATE              DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT DIGITAL_IDENTITY_PK PRIMARY KEY (DIGITAL_IDENTITY_ID)
);
CREATE TABLE ACCESS_CHANNEL_CODE
(
    ACCESS_CHANNEL_CODE VARCHAR2(10)           NOT NULL,
    LABEL               VARCHAR2(30),
    DESCRIPTION         VARCHAR2(255),
    DISPLAY_ORDER       NUMBER DEFAULT 1       NOT NULL,
    EFFECTIVE_DATE      DATE                   NOT NULL,
    EXPIRY_DATE         DATE                   NOT NULL,
    CREATE_USER         VARCHAR2(32)           NOT NULL,
    CREATE_DATE         DATE   DEFAULT SYSDATE NOT NULL,
    UPDATE_USER         VARCHAR2(32)           NOT NULL,
    UPDATE_DATE         DATE   DEFAULT SYSDATE NOT NULL,
    CONSTRAINT ACCESS_CHANNEL_CODE_PK PRIMARY KEY (ACCESS_CHANNEL_CODE)
);
CREATE TABLE IDENTITY_TYPE_CODE
(
    IDENTITY_TYPE_CODE VARCHAR2(10)           NOT NULL,
    LABEL              VARCHAR2(30),
    DESCRIPTION        VARCHAR2(255),
    DISPLAY_ORDER      NUMBER DEFAULT 1       NOT NULL,
    EFFECTIVE_DATE     DATE                   NOT NULL,
    EXPIRY_DATE        DATE                   NOT NULL,
    CREATE_USER        VARCHAR2(32)           NOT NULL,
    CREATE_DATE        DATE   DEFAULT SYSDATE NOT NULL,
    UPDATE_USER        VARCHAR2(32)           NOT NULL,
    UPDATE_DATE        DATE   DEFAULT SYSDATE NOT NULL,
    CONSTRAINT IDENTITY_TYPE_CODE_PK PRIMARY KEY (IDENTITY_TYPE_CODE)
);
--Comments on tables--
COMMENT ON TABLE ACCESS_CHANNEL_CODE IS 'Access Channel Code lists the various channels (applications or services) that make use of the student Digital Identity records to access Education Services. Examples are the Online Student PEN Retrieval and the Student Transcript Service.';
COMMENT ON TABLE IDENTITY_TYPE_CODE IS 'Identity Type Code lists the types of digital identities supported. Examples are BC Services Card and Basic BCeID.';
COMMENT ON TABLE DIGITAL_IDENTITY IS 'A Digital Identity is used by a specific student to access Education services. Types of digital identities supported include BC Services Card and Basic BCeID.';

--Alter tables--
ALTER TABLE DIGITAL_IDENTITY
    ADD CONSTRAINT UQ_DIGITAL_ID_USER_VAL_TYPE UNIQUE (IDENTITY_TYPE_CODE, IDENTITY_VALUE);

ALTER TABLE DIGITAL_IDENTITY
    ADD CONSTRAINT FK_DIGITAL_IDENT_IDENT_TYPE_CODE FOREIGN KEY (IDENTITY_TYPE_CODE) REFERENCES IDENTITY_TYPE_CODE (IDENTITY_TYPE_CODE);

ALTER TABLE DIGITAL_IDENTITY
    ADD CONSTRAINT FK_DIGITAL_IDENT_ACCESS_CHAN_CODE FOREIGN KEY (LAST_ACCESS_CHANNEL_CODE) REFERENCES ACCESS_CHANNEL_CODE (ACCESS_CHANNEL_CODE);

--Inserts for code tables--

INSERT INTO ACCESS_CHANNEL_CODE (ACCESS_CHANNEL_CODE, LABEL, DESCRIPTION, DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE,
                                 CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('OSPR', 'Online Student PEN Retrieval',
        'The Online Student PEN Retrieval (OSPR) application used by Students to get their PEN value. EDUC staff use the app to fulfill retrieval reqeusts.',
        1, to_date('2020-01-01', 'YYYY-MM-DD'), to_date('2099-12-31', 'YYYY-MM-DD'), 'IDIR/GRCHWELO',
        to_date('2019-11-07', 'YYYY-MM-DD'), 'IDIR/GRCHWELO', to_date('2019-11-07', 'YYYY-MM-DD'));

INSERT INTO ACCESS_CHANNEL_CODE (ACCESS_CHANNEL_CODE, LABEL, DESCRIPTION, DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE,
                                 CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('STS', 'Student Transcripts Service',
        'The Student Transcript Service that is used by Students to either get a copy of their high-school transcript or to send copies to schools, employers, etc.',
        2, to_date('2020-01-01', 'YYYY-MM-DD'), to_date('2099-12-31', 'YYYY-MM-DD'), 'IDIR/GRCHWELO',
        to_date('2019-11-07', 'YYYY-MM-DD'), 'IDIR/GRCHWELO', to_date('2019-11-07', 'YYYY-MM-DD'));

INSERT INTO IDENTITY_TYPE_CODE (IDENTITY_TYPE_CODE, LABEL, DESCRIPTION, DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE,
                                CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('BCSC', 'BC Services Card', 'Digital Identity via a BC Services Card, serviced by CITZ/IDIM.', 1,
        to_date('2020-01-01', 'YYYY-MM-DD'), to_date('2099-12-31', 'YYYY-MM-DD'), 'IDIR/GRCHWELO',
        to_date('2019-11-07', 'YYYY-MM-DD'), 'IDIR/GRCHWELO', to_date('2019-11-07', 'YYYY-MM-DD'));

INSERT INTO IDENTITY_TYPE_CODE (IDENTITY_TYPE_CODE, LABEL, DESCRIPTION, DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE,
                                CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('BASIC', 'Basic BCeID', 'Digital Identity via a Basic BCeID, serviced by CITZ/IDIM.', 2,
        to_date('2020-01-01', 'YYYY-MM-DD'), to_date('2099-12-31', 'YYYY-MM-DD'), 'IDIR/GRCHWELO',
        to_date('2019-11-07', 'YYYY-MM-DD'), 'IDIR/GRCHWELO', to_date('2019-11-07', 'YYYY-MM-DD'));

INSERT INTO IDENTITY_TYPE_CODE (IDENTITY_TYPE_CODE, LABEL, DESCRIPTION, DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE,
                                CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('PERSONAL', 'Personal BCeID', 'Digital Identity via a Personal BCeID, serviced by CITZ/IDIM.', 3,
        to_date('2020-01-01', 'YYYY-MM-DD'), to_date('2099-12-31', 'YYYY-MM-DD'), 'IDIR/GRCHWELO',
        to_date('2019-11-07', 'YYYY-MM-DD'), 'IDIR/GRCHWELO', to_date('2019-11-07', 'YYYY-MM-DD'));

CREATE INDEX DIGITAL_IDENTITY_IDENTITY_VALUE_I ON DIGITAL_IDENTITY ( IDENTITY_VALUE );