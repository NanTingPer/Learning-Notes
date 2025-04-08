create table dws_ds_hudi.user_consumption_day_aggr(
    uuid string,
    user_id int,
    user_name string,
    total_amount double,
    total_count int,
    year int,
    month int,
    day int)
using hudi
tblproperties(
    type='mor',
    primaryKey='uuid',
    preCombineField='total_count')
partitioned by('year','month','day');

create table dws_ds_hudi.province_consumption_day_aggr(
    uuid string,
    province_id int,
    province_name string,
    region_id int,
    region_name string,
    total_amount double,
    total_count int,
    sequence int,
    year int,
    month int
) using hudi
tblproperties(
    type = 'mor',
    primaryKey = 'uuid',
    preCombineField='total_count')
partitioned by(year, month)

create table shtd_result.topten(topquantityid Int32,topquantityname TEXT,topquantity Int32,toppriceid TEXT,toppricename TEXT,topprice DECIMAL(32,8),sequence Int32)

create table shtd_result.topten(
    topquantityid Int32,
    topquantityname TEXT,
    topquantity Int32,
    toppriceid TEXT,
    toppricename TEXT,
    topprice DECIMAL(32,8),
    sequence Int32
)


create table shtd_result.nationmedian(provinceid Int32,provincename TEXT,regionid Int32,regionname TEXT,provincemedian DOUBLE,regionmedian DOUBLE)
create table shtd_result.nationmedian(
    provinceid Int32,
    provincename TEXT,
    regionid Int32,
    regionname TEXT,
    provincemedian DOUBLE,
    regionmedian DOUBLE
)