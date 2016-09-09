    
    create table public.documents (
        id  bigserial not null,
        active_date int8,
        category_id varchar(100),
        content varchar(1048576),
        created_by varchar(255),
        date_created int8,
        date_modified int8,
        deleted boolean,
        description varchar(255),
        doc_id varchar(100),
        doc_type varchar(255),
        doc_url varchar(255),
        doc_name varchar(255),
        exp_date int8,
        is_template boolean,
        modified_by varchar(255),
        status_id varchar(100),
        subcategory_id varchar(100),
        primary key (id)
    ); 

    create table public.variables (
        id  bigserial not null,
        created_by varchar(255),
        date_created int8,
        date_modified int8,
        modified_by varchar(255),
        value varchar(255),
        varkey varchar(100) not null,
        primary key (id)
    ); 
     
    alter table public.variables 
        add constraint UK_g86npvalrgjbior19e6tny5pp unique (varkey);