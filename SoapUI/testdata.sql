/* 1. Bruker som sist oppdaterte/verifserte kontaktinfoen sin i 2012, skal få "KAN_IKKE_VARSLES"*/
update user set email='demo1-oppdatert-2012@difi.no' ,
mobile=98765432 ,
mobile_verified_date='2012-01-01 00:00:00', 
email_verified_date='2012-01-01 00:00:00' , 
email_last_updated='2012-01-01', 
mobile_last_updated='2012-01-01', 
reserved=0, 
deleted=0 where ssn=23079400440;

/* 2. Bruker som sist oppdaterte/verifserte kontaktinfoen sin i 2015, skal få "KAN_VARSLES"*/
update user set email='demo2-oppdatert-2015@difi.no' ,
mobile=98765432 ,
mobile_verified_date='2015-01-01 00:00:00', 
email_verified_date='2015-01-01 00:00:00' , 
email_last_updated='2015-01-01', 
mobile_last_updated='2015-01-01', 
reserved=0, 
deleted=0 where ssn=23079400602;

/* 3. Bruker som er sletta (men ellers har oppdatart i 2015) skal få "KAN_IKKE_VARSLES" samt "SLETTET"*/
update user set email='demo3-sletta@difi.no',
mobile=98765432 ,
mobile_verified_date='2015-01-01 00:00:00', 
email_verified_date='2015-01-01 00:00:00' , 
email_last_updated='2015-01-01', 
mobile_last_updated='2015-01-01', 
reserved=0, 
deleted=1 where ssn=23079400793;

/* 4. Bruker som er reservert (men ellers har oppdatart i 2015) skal få "KAN_IKKE_VARSLES"*/
update user set email='demo4-reservert@difi.no' ,
mobile=98765432 ,
mobile_verified_date='2015-01-01 00:00:00', 
email_verified_date='2015-01-01 00:00:00' , 
email_last_updated='2015-01-01', 
mobile_last_updated='2015-01-01', 
reserved=1, 
deleted=0 where ssn=23079400874;

/* 5. Bruker som ein ikkje finn i DB skal få "KAN_IKKE_VARSLES"*/


/* Finn brukere til demo */
select ssn, email, email_verified_date, email_last_updated, mobile, mobile_verified_date, mobile_last_updated, reserved, deleted from user where email like "%@difi.no";
