--Users
CREATE USER STUDENT IDENTIFIED BY &mypassword;
GRANT create session TO STUDENT;
GRANT create table TO STUDENT;
GRANT create view TO STUDENT;
GRANT create trigger TO STUDENT;
GRANT create procedure TO STUDENT;
GRANT create sequence TO STUDENT;
GRANT create synonym TO STUDENT;

ALTER USER STUDENT QUOTA UNLIMITED ON USERS;
ALTER USER PROXY_STUDENT QUOTA UNLIMITED ON USERS;

--Tables
CREATE TABLE STUDENT.STUDENT ( 
  STUDENT_ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),  
  PEN VARCHAR2(9) CONSTRAINT PEN_UNIQUE UNIQUE,  
  LEGAL_FIRST_NAME VARCHAR2(40),
  LEGAL_MIDDLE_NAMES VARCHAR2(255),
  LEGAL_LAST_NAME VARCHAR2(40),
  DOB DATE,
  SEX_CODE VARCHAR2(1),
  GENDER_CODE VARCHAR2(1),
  DATA_SOURCE_CODE VARCHAR2(10),
  USUAL_FIRST_NAME VARCHAR2(40),
  USUAL_MIDDLE_NAMES VARCHAR2(255),
  USUAL_LAST_NAME VARCHAR2(40),
  EMAIL VARCHAR2(255),
  DECEASED_DATE DATE,
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT STUDENT_PK PRIMARY KEY (STUDENT_ID)  
);  

CREATE TABLE STUDENT.ADDRESS (
  ADDRESS_ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),  
  STUDENT_ID NUMBER NOT NULL,  
  ADDRESS_TYPE_CODE VARCHAR2(10) NOT NULL,
  ADDRESS_LINE_1 VARCHAR2(255),
  ADDRESS_LINE_2 VARCHAR2(255),
  CITY VARCHAR2(50),
  PROVINCE_CODE VARCHAR2(2),
  COUNTRY_CODE VARCHAR2(3),
  POSTAL_CODE VARCHAR2(7),
  DATA_SOURCE_CODE VARCHAR2(10),
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE,
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT ADDRESS_PK PRIMARY KEY (ADDRESS_ID)  
);

CREATE TABLE STUDENT.DIGITAL_IDENTITY (
  DIGITAL_IDENTITY_ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),  
  STUDENT_ID NUMBER,
  IDENTITY_TYPE_CODE VARCHAR2(10) NOT NULL,
  IDENTITY_VALUE VARCHAR2(255) NOT NULL,
  LAST_ACCESS_DATE DATE NOT NULL,
  LAST_ACCESS_CHANNEL_CODE VARCHAR2(10) NOT NULL,
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT DIGITAL_IDENTITY_PK PRIMARY KEY (DIGITAL_IDENTITY_ID)  
);

CREATE TABLE STUDENT.PEN_RETRIEVAL_REQUEST (
  PEN_RETRIEVAL_REQUEST_ID NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
  DIGITAL_IDENTITY_ID NUMBER NOT NULL,
  PEN_RETRIEVAL_REQUEST_STATUS_CODE VARCHAR2(10),
  LEGAL_FIRST_NAME VARCHAR2(40),
  LEGAL_MIDDLE_NAMES VARCHAR2(255),
  LEGAL_LAST_NAME VARCHAR2(40),
  DOB DATE,
  SEX_CODE VARCHAR2(1),
  GENDER_CODE VARCHAR2(1),
  DATA_SOURCE_CODE VARCHAR2(10),
  USUAL_FIRST_NAME VARCHAR2(40),
  USUAL_MIDDLE_NAMES VARCHAR2(255),
  USUAL_LAST_NAME VARCHAR2(40),
  EMAIL VARCHAR2(255),
  MAIDEN_NAME VARCHAR2(40),
  PAST_NAMES VARCHAR2(255),
  LAST_BC_SCHOOL VARCHAR2(255),
  LAST_BC_SCHOOL_STUDENT_NUMBER VARCHAR2(12),
  CURRENT_SCHOOL VARCHAR2(255),
  ADDRESS_LINE_1 VARCHAR2(255),
  ADDRESS_LINE_2 VARCHAR2(255),
  CITY VARCHAR2(50),
  PROVINCE_CODE VARCHAR2(2),
  COUNTRY_CODE VARCHAR2(3),
  POSTAL_CODE VARCHAR2(7),
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT PEN_RETRIEVAL_REQUEST_PK PRIMARY KEY (PEN_RETRIEVAL_REQUEST_ID)  
);

-- Lookup Code Tables
CREATE TABLE STUDENT.ACCESS_CHANNEL_CODE (
  ACCESS_CHANNEL_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT ACCESS_CHANNEL_CODE_PK PRIMARY KEY (ACCESS_CHANNEL_CODE)  
);

CREATE TABLE STUDENT.ADDRESS_TYPE_CODE (
  ADDRESS_TYPE_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT ADDRESS_TYPE_CODE_PK PRIMARY KEY (ADDRESS_TYPE_CODE)  
);

CREATE TABLE STUDENT.DATA_SOURCE_CODE (
  DATA_SOURCE_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT DATA_SOURCE_CODE_PK PRIMARY KEY (DATA_SOURCE_CODE)  
);

CREATE TABLE STUDENT.GENDER_CODE (
  GENDER_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT GENDER_CODE_PK PRIMARY KEY (GENDER_CODE)  
);

CREATE TABLE STUDENT.IDENTITY_TYPE_CODE (
  IDENTITY_TYPE_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT IDENTITY_TYPE_CODE_PK PRIMARY KEY (IDENTITY_TYPE_CODE)  
);

CREATE TABLE STUDENT.PEN_RETRIEVAL_REQUEST_STATUS_CODE (
  PEN_RETRIEVAL_REQUEST_STATUS_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT PEN_RETRIEVAL_REQUEST_STATUS_CODE_PK PRIMARY KEY (PEN_RETRIEVAL_REQUEST_STATUS_CODE)  
);

CREATE TABLE STUDENT.SEX_CODE (
  SEX_CODE VARCHAR2(10) NOT NULL,
  LABEL VARCHAR2(30) NOT NULL,
  DESCRIPTION VARCHAR2(255) NOT NULL,
  EFFECTIVE_DATE DATE NOT NULL,
  EXPIRY_DATE DATE NOT NULL,  
  CREATE_USER VARCHAR2(32) NOT NULL,
  CREATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  UPDATE_USER VARCHAR2(32) NOT NULL,
  UPDATE_DATE DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT SEX_CODE_PK PRIMARY KEY (SEX_CODE)  
);



--Foreign key constraints
alter table STUDENT.STUDENT add constraint FK_STUDENT_GENDER_CODE foreign key (GENDER_CODE) references STUDENT.GENDER_CODE (GENDER_CODE);
alter table STUDENT.STUDENT add constraint FK_STUDENT_SEX_CODE foreign key (SEX_CODE) references STUDENT.SEX_CODE (SEX_CODE);
alter table STUDENT.STUDENT add constraint FK_STUDENT_DATA_SOURCE_CODE foreign key (DATA_SOURCE_CODE) references STUDENT.DATA_SOURCE_CODE (DATA_SOURCE_CODE);

alter table STUDENT.ADDRESS add constraint FK_ADDRESS_STUDENT_ID foreign key (STUDENT_ID) references STUDENT.STUDENT (STUDENT_ID);
alter table STUDENT.ADDRESS add constraint FK_ADDRESS_DATA_SOURCE_CODE foreign key (DATA_SOURCE_CODE) references STUDENT.DATA_SOURCE_CODE (DATA_SOURCE_CODE);
alter table STUDENT.ADDRESS add constraint FK_ADDRESS_ADDRESS_TYPE_CODE foreign key (ADDRESS_TYPE_CODE) references STUDENT.ADDRESS_TYPE_CODE (ADDRESS_TYPE_CODE);

alter table STUDENT.DIGITAL_IDENTITY add constraint FK_DIGITAL_IDENTITY_STUDENT_ID foreign key (STUDENT_ID) references STUDENT.STUDENT (STUDENT_ID);
alter table STUDENT.DIGITAL_IDENTITY add constraint FK_DIGITAL_IDENT_IDENT_TYPE_CODE foreign key (IDENTITY_TYPE_CODE) references STUDENT.IDENTITY_TYPE_CODE (IDENTITY_TYPE_CODE);
alter table STUDENT.DIGITAL_IDENTITY add constraint FK_DIGITAL_IDENT_ACCESS_CHAN_CODE foreign key (LAST_ACCESS_CHANNEL_CODE) references STUDENT.ACCESS_CHANNEL_CODE (ACCESS_CHANNEL_CODE);

alter table STUDENT.PEN_RETRIEVAL_REQUEST add constraint FK_PEN_RETRIEVAL_REQUEST_DIGITAL_IDENTITY_ID foreign key (DIGITAL_IDENTITY_ID) references STUDENT.DIGITAL_IDENTITY (DIGITAL_IDENTITY_ID);
alter table STUDENT.PEN_RETRIEVAL_REQUEST add constraint FK_PEN_RETRIEVAL_REQUEST_PEN_RETRIEVAL_REQUEST_STATUS_CODE foreign key (PEN_RETRIEVAL_REQUEST_STATUS_CODE) references STUDENT.PEN_RETRIEVAL_REQUEST_STATUS_CODE (PEN_RETRIEVAL_REQUEST_STATUS_CODE);
alter table STUDENT.PEN_RETRIEVAL_REQUEST add constraint FK_PEN_RETRIEVAL_REQUEST_DATA_SOURCE_CODE foreign key (DATA_SOURCE_CODE) references STUDENT.DATA_SOURCE_CODE (DATA_SOURCE_CODE);
alter table STUDENT.PEN_RETRIEVAL_REQUEST add constraint FK_PEN_RETRIEVAL_REQUEST_GENDER_CODE foreign key (GENDER_CODE) references STUDENT.GENDER_CODE (GENDER_CODE);
alter table STUDENT.PEN_RETRIEVAL_REQUEST add constraint FK_PEN_RETRIEVAL_REQUEST_SEX_CODE foreign key (SEX_CODE) references STUDENT.SEX_CODE (SEX_CODE);


-- Table Comments
COMMENT ON TABLE Student.Student IS 'Student contains core identifying data for students, include PEN, names, DOB, sex, etc.';
COMMENT ON TABLE Student.Digital_Identity IS 'A Digital Identity is used by a specific student to access Education services. Types of digital identities supported include BC Services Card and Basic BCeID.';
COMMENT ON TABLE Student.PEN_Retrieval_Request IS 'PEN Retrieval Request is a transaction record of a request by a student to retrieve their PEN.';
COMMENT ON TABLE Student.Address IS 'Address holds address information for students.';

COMMENT ON TABLE Student.Access_Channel_Code IS 'Access Channel Code lists the various channels (applications or services) that make use of the student Digital Identity records to access Education Services. Examples are the Online Student PEN Retrieval and the Student Transcript Service.';
COMMENT ON TABLE Student.Address_Type_Code IS 'Address Type Code lists the types of addresses. Examples are Mailing, Physical, and Delivery.';
COMMENT ON TABLE Student.Data_Source_Code IS 'Data Source Code lists the sources for student data. Examples are BC Services Card, MyEducation BC, Birth Certificate.';
COMMENT ON TABLE Student.Gender_Code IS 'Gender Code lists the standard codes for Gender: Female, Male, Diverse.';
COMMENT ON TABLE Student.Identity_Type_Code IS 'Identity Type Code lists the types of digital identities supported. Examples are BC Services Card and Basic BCeID.';
COMMENT ON TABLE Student.PEN_Retrieval_Request_Status_Code IS 'PEN Retrieval Request Status Code lists the transaction status values for the PEN Retrieval Requests. Examples are Submitted, Pending Student Input, Completed, Rejected.';
COMMENT ON TABLE Student.Sex_Code IS 'Sex Code lists the standard codes for Sex: Female, Male, Intersex.';


-- Column Comments
COMMENT ON COLUMN Student.Student_ID IS 'Unique surrogate key for each Student. Generated as an identity/sequence value';
COMMENT ON COLUMN Student.PEN IS 'Provincial Education Number assigned by system to this student, in SIN format; used to track a student all through their educational career. ';
COMMENT ON COLUMN Student.Legal_First_Name IS 'The legal first name of the student';
COMMENT ON COLUMN Student.Legal_Middle_Names IS 'The legal middle names of the student';
COMMENT ON COLUMN Student.Legal_Last_Name IS 'The legal last name of the student';
COMMENT ON COLUMN Student.DOB IS 'The date of birth of the student';
COMMENT ON COLUMN Student.Sex_Code IS 'The sex of the student';
COMMENT ON COLUMN Student.Gender_Code IS 'The gender of the student';
COMMENT ON COLUMN Student.Data_Source_Code IS 'Code indicating the primary data source for the Student data';
COMMENT ON COLUMN Student.Usual_First_Name IS 'The usual/preferred first name of the student';
COMMENT ON COLUMN Student.Usual_Middle_Names IS 'The usual/preferred middle name of the student';
COMMENT ON COLUMN Student.Usual_Last_Name IS 'The usual/preferred last name of the student';
COMMENT ON COLUMN Student.Email IS 'The email address of the student';
COMMENT ON COLUMN Student.Deceased_Date IS 'The date of death for the student. Will be known to EDUC only if student was an active student at the time.';

COMMENT ON COLUMN Address.Student_ID IS 'Foreign key to Student table identifying Student that is identified by the Digital Identity';
COMMENT ON COLUMN Address.Address_Type_Code IS 'Code indicating the type of the address.';
COMMENT ON COLUMN Address.Address_Line_1 IS 'Line 1 of address';
COMMENT ON COLUMN Address.Address_Line_2 IS 'Line 2 of address';
COMMENT ON COLUMN Address.City IS 'Name of city or municipality for the address';
COMMENT ON COLUMN Address.Province_Code IS 'Province or State as the Canada Post 2 char code';
COMMENT ON COLUMN Address.Country_Code IS 'Country of address, as the ISO 3 char code';
COMMENT ON COLUMN Address.Postal_Code IS 'Postal Code for the address. Format: ANA NAN';
COMMENT ON COLUMN Address.Data_Source_Code IS 'Source of the data for the address';

COMMENT ON COLUMN Digital_Identity.Digital_Identity_ID IS 'Unique surrogate key for each Digital Identity. Generated as an identity/sequence value.';
COMMENT ON COLUMN Digital_Identity.Student_ID IS 'Foreign key to Student table identifying Student that is identified by the Digital Identity';
COMMENT ON COLUMN Digital_Identity.Identity_Type_Code IS 'Code indicating the type of the digital identity.';
COMMENT ON COLUMN Digital_Identity.Identity_Value IS 'Value of the digital identifier. May be a string, a GUID, a BCSC DID, etc.';
COMMENT ON COLUMN Digital_Identity.Last_Access_Date IS 'The date and time of the last access to the system based on this digital identity.';
COMMENT ON COLUMN Digital_Identity.Last_Access_Channel_Code IS 'Code indicating the channel or application that this digital identity accessed.';

COMMENT ON COLUMN PEN_Retrieval_Request.PEN_Retrieval_Request_ID IS 'Unique surrogate key for each PEN Retrieval request. Generated as an identity/sequence value.';
COMMENT ON COLUMN PEN_Retrieval_Request.Digital_Identity_ID IS 'Foreign key to Digital Identity table identifying the Digital Identity that is was used to make this request';
COMMENT ON COLUMN PEN_Retrieval_Request.PEN_Retrieval_Request_Status_Code IS 'Code indicating the status of the Student PEN Retrieval request';
COMMENT ON COLUMN PEN_Retrieval_Request.Legal_First_Name IS 'The legal first name of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Legal_Middle_Names IS 'The legal middle names of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Legal_Last_Name IS 'The legal last name of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.DOB IS 'The date of birth of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Sex_Code IS 'The sex of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Gender_Code IS 'The gender of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Data_Source_Code IS 'Code indicating the primary data source for the Student data';
COMMENT ON COLUMN PEN_Retrieval_Request.Usual_First_Name IS 'The usual/preferred first name of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Usual_Middle_Names IS 'The usual/preferred middle name of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Usual_Last_Name IS 'The usual/preferred last name of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Email IS 'Email of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Maiden_Name IS 'Maiden Name of the student, if applicable';
COMMENT ON COLUMN PEN_Retrieval_Request.Past_Names IS 'Past Names of the student';
COMMENT ON COLUMN PEN_Retrieval_Request.Last_BC_School IS 'Name of last BC school that the student attended';
COMMENT ON COLUMN PEN_Retrieval_Request.Last_BC_School_Student_Number IS 'Student Number assigned to student at the last BC school attended';
COMMENT ON COLUMN PEN_Retrieval_Request.Current_School IS 'Name of current BC school, if applicable';
COMMENT ON COLUMN PEN_Retrieval_Request.Address_Line_1 IS 'Line 1 of address';
COMMENT ON COLUMN PEN_Retrieval_Request.Address_Line_2 IS 'Line 2 of address';
COMMENT ON COLUMN PEN_Retrieval_Request.City IS 'Name of city or municipality for the address';
COMMENT ON COLUMN PEN_Retrieval_Request.Province_Code IS 'Province or State as the Canada Post 2 char code';
COMMENT ON COLUMN PEN_Retrieval_Request.Country_Code IS 'Country of address, as the ISO 3 char code';
COMMENT ON COLUMN PEN_Retrieval_Request.Postal_Code IS 'Postal Code for the address. Format: ANA NAN';


--ORDS Enabled Schema/Tables

DECLARE
  PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    ORDS.ENABLE_SCHEMA(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_url_mapping_type => 'BASE_PATH',
                       p_url_mapping_pattern => 'student',
                       p_auto_rest_auth => FALSE);
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'STUDENT',
                       p_object_type => 'TABLE',
                       p_object_alias => 'student',
                       p_auto_rest_auth => FALSE);
                       
	ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'ADDRESS',
                       p_object_type => 'TABLE',
                       p_object_alias => 'address',
                       p_auto_rest_auth => FALSE);

    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'DIGITAL_IDENTITY',
                       p_object_type => 'TABLE',
                       p_object_alias => 'digital_identity',
                       p_auto_rest_auth => FALSE);

    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'PEN_RETRIEVAL_REQUEST',
                       p_object_type => 'TABLE',
                       p_object_alias => 'pen_retrieval_request',
                       p_auto_rest_auth => FALSE);    
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'IDENTITY_TYPE_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'identity_type_code',
                       p_auto_rest_auth => FALSE);   
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'ACCESS_CHANNEL_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'access_channel_code',
                       p_auto_rest_auth => FALSE);   
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'ADDRESS_TYPE_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'aaddress_type_code',
                       p_auto_rest_auth => FALSE);   
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'GENDER_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'gender_code',
                       p_auto_rest_auth => FALSE);   
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'SEX_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'sex_code',
                       p_auto_rest_auth => FALSE);   
                       
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'DATA_SOURCE_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'data_source_code',
                       p_auto_rest_auth => FALSE);   

    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'STUDENT',
                       p_object => 'PEN_RETRIEVAL_REQUEST_STATUS_CODE',
                       p_object_type => 'TABLE',
                       p_object_alias => 'pen_retrieval_request_status_code',
                       p_auto_rest_auth => FALSE);   
                       
    commit;

END;