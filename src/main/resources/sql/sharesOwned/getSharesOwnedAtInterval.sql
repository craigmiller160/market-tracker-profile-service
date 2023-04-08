WITH the_dates AS (
    SELECT CAST(date_trunc('day', dd) AS date) AS the_date
    FROM generate_series(
        CAST(:startDate AS date),
        CAST(:endDate AS date),
        CAST(:interval AS interval)
    ) dd
)
SELECT td.the_date AS date, so.user_id, so.symbol, so.total_shares
{{#portfolioId}}
so.portfolio_id
{{/portfolioId}}
FROM the_dates td
JOIN shares_owned so ON td.the_date <@ so.date_range
WHERE so.user_id = :userId
{{#portfolioId}}
AND so.portfolioId = :portfolioId
{{/portfolioId}}