insert into WA_WORKSTATION (AIRPORT_CODE, WAREHOUSE_CODE,SECTION,TYPE,NAME,COMPATIBLE_TYPES,SIZE,SHC,PRODUCT_TYPE,OPEN,
BREAK_TIME_START, BREAK_TIME_END,SERVICEABLE,MULTIPLE_ULD_ALLOWED,FIXED,NOTIFICATION_TIME,ACTIVE)
values('DXB','WH1','SEC1','M','WORKSTATION','AKE-AKN','12ft','PER-RFL','GCR','Y',null,null,'Y','Y','Y',null,'Y');


--- JOB
insert into WA_JOB(CODE,TYPE,START_TIME, END_TIME,CREATED_BY, CREATED_DATE, MODIFIED_BY, MODIFIED_DATE,DURATION,STATUS)
values ('JOB1','BREAKDOWN',null,null,'USER1',null,null,null,30,'C');
