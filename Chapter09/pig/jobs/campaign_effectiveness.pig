click_thru_data = LOAD '../click_thru_data.txt' using PigStorage(' ')
  AS (cookie_id:chararray,
      campaign_id:chararray,
      product_id:chararray,
      click:chararray);

click_thrus = FILTER click_thru_data BY click == 'true';
distinct_click_thrus = DISTINCT click_thrus;
distinct_click_thrus_by_campaign = GROUP distinct_click_thrus BY campaign_id;
--count_of_click_thrus_by_campaign = FOREACH distinct_click_thrus_by_campaign GENERATE group, COUNT(distinct_click_thrus);
count_of_click_thrus_by_campaign = FOREACH distinct_click_thrus_by_campaign GENERATE group, COUNT($1);
dump count_of_click_thrus_by_campaign;

impressions_by_campaign = GROUP click_thru_data BY campaign_id;
count_of_impressions_by_campaign = FOREACH impressions_by_campaign GENERATE group, COUNT($1);
dump count_of_impressions_by_campaign;

joined_data = JOIN count_of_impressions_by_campaign BY $0 LEFT OUTER, count_of_click_thrus_by_campaign BY $0 USING 'replicated';
dump joined_data;

result = FOREACH joined_data GENERATE $0 as campaign, ($3 is null ? 0 : $3) as clicks, $1 as impressions, (double)$3/(double)$1 as effectiveness:double;
dump result;
