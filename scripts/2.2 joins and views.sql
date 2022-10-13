--2.1
SELECT a.AUFTRNR, a.DATUM 
FROM AUFTRAG a, KUNDE k 
WHERE a.KUNDNR IN(k.NR) AND k.NAME IN('Fahrrad Shop')

--2.2
SELECT a.AUFTRNR, a.DATUM 
FROM AUFTRAG a, KUNDE k 
WHERE a.KUNDNR = SOME(k.NR) AND k.NAME = SOME('Fahrrad Shop')

--2.3
SELECT a.AUFTRNR, a.DATUM
FROM AUFTRAG a, KUNDE k 
WHERE EXISTS(SELECT a.AUFTRNR FROM AUFTRAG, KUNDE WHERE a.KUNDNR = k.NR AND k.NAME = SOME('Fahrrad Shop'))

--2.4
SELECT KUNDNR, COUNT(*) AS anzahl, MIN(DATUM) AS von, MAX(DATUM) AS bis 
FROM AUFTRAG a, KUNDE k 
WHERE a.KUNDNR = k.NR
GROUP BY KUNDNR

--2.5
SELECT KUNDNR, COUNT(*) AS anzahl, MIN(DATUM) AS von, MAX(DATUM) AS bis 
FROM AUFTRAG a, KUNDE k 
WHERE a.KUNDNR = k.NR
GROUP BY KUNDNR
HAVING COUNT(*) = 1

--2.6
SELECT k.NR, k.NAME, a.AUFTRNR AS auftrag 
FROM KUNDE k 
INNER JOIN AUFTRAG a ON a.KUNDNR = k.NR
ORDER BY k.NR 

--2.7
SELECT k.NR, k.NAME, a.AUFTRNR AS auftrag, p.NAME 
FROM KUNDE k 
INNER JOIN AUFTRAG a ON a.KUNDNR = k.NR
INNER JOIN PERSONAL p ON a.PERSNR = p.PERSNR 
ORDER BY k.NR 

--2.8
SELECT k.NAME, COUNT(*) AS anzahl
FROM AUFTRAG a 
INNER JOIN KUNDE k ON KUNDNR = k.NR
GROUP BY a.KUNDNR, k.NAME 
HAVING COUNT(*) = (SELECT MAX(COUNT(*)) FROM AUFTRAG GROUP BY KUNDNR) 

--2.9
WITH hilfstabelle
AS (SELECT k.NAME AS name, COUNT(*) AS anzahl
FROM AUFTRAG a 
INNER JOIN KUNDE k ON KUNDNR = k.NR
GROUP BY a.KUNDNR, k.NAME)
SELECT h.name, h.anzahl
FROM hilfstabelle h

--2.10
CREATE OR REPLACE VIEW KundenUmsatz AS
SELECT k.name, SUM(ap.GESAMTPREIS) AS summe 
FROM AUFTRAG a, AUFTRAGSPOSTEN ap, KUNDE k 
WHERE (a.AUFTRNR=ap.AUFTRNR) AND (a.KUNDNR=k.NR)
GROUP BY k.NAME, k.NR

SELECT *
FROM KundenUmsatz

--2.11
DROP VIEW KundenUmsatz


