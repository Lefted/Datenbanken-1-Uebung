--2.3 (Turn to manual commit in dbeaver)
BEGIN;

SELECT COUNT(*) FROM LIEFERUNG l

DELETE FROM LIEFERANT l 

SELECT COUNT(*) FROM LIEFERUNG l 

ROLLBACK

SELECT COUNT(*) FROM LIEFERUNG l 
