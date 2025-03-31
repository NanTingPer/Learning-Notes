--ERRO1
create table shtd_result.topten(topquantityid Int32, topquantityname TEXT, topquantity Int32, toppriceid TEXT, toppricename TEXT, topprice decimal(20,2), sequence Int32) ENGINE MergeTree order by sequence;
select * from shtd_result.topten order by sequence limit 5;

--ERRO2
create table shtd_result.nationmedian (provinceid Int32, provincename TEXT, regionid Int32, regionname TEXT, provincemedian DOUBLE, regionmedian DOUBLE)ENGINE MergeTree order by provinceid;
select * from shtd_result.nationmedian order by regionid, provinceid limit 5;

--ERRO3
select * from shtd_result.provinceavgcmp order by provinceid desc, provinceavgconsumption desc limit 5;

--ERRO4
select * from provinceavgcmpregion order by provinceid desc,provinceavgconsumption desc,regionavgconsumption desc limit 5;

--ERRO5
create table shtd_result.regiontopthree(


)