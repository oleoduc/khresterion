    create table dbo.documents (
        id bigint identity not null,
        active_date bigint,
        category_id varchar(100),
        content varchar(MAX),
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        deleted bit,
        description varchar(255),
        doc_id varchar(100),
        doc_type varchar(255),
        doc_url varchar(255),
        doc_name varchar(255),
        exp_date bigint,
        is_template bit,
        modified_by varchar(255),
        status_id varchar(100),
        subcategory_id varchar(100),
        primary key (id)
    )
    GO
    
    create table dbo.variables (
        id bigint identity not null,
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        modified_by varchar(255),
        value varchar(255),
        varkey varchar(100) not null,
        primary key (id)
    )
    GO
    
    alter table dbo.variables 
        add constraint UK_g86npvalrgjbior19e6tny5pp  unique (varkey)
        GO
        