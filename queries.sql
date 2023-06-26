SELECT * FROM articles
LEFT JOIN feeds ON feed_id = id
WHERE id IS NULL;

SELECT title, COUNT(*) as count, feed_id
FROM articles
GROUP BY title
HAVING count > 1;