create table dws_ds_hudi.user_consumption_day_aggr2(
uuid string,
user_id int,
user_name string,
total_amount double,
total_count int,
year int,
month int,
day int)
using hudi
tblproperties (
type='mor',
primaryKey='uuid',
preCombineField='total_count')
partitioned by(year ,month ,day )
location '/user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr2';


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
    month int)
using hudi
tblproperties(
    type = 'mor',
    primaryKey = 'uuid',
    preCombineField = 'total_count')
partitioned by(year, month)


select
    uuid,
    province_id,
    province_name,
    region_id,
    region_name,
    CAST(total_amount as bigint) as total_amount,
    total_count,
    sequence,
    year,
    month
from dws_ds_hudi.province_consumption_day_aggr
order by
    total_count desc,
    total_amount desc,
    province_id desc
limit 5




