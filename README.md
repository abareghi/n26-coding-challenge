#Description
This is a coding challenge prepared for N26.

#Build and Run
- To build the project run "mvn clean package"
- To run the project, after build, from the project root folder run " java -jar target/n26-0.0.1-SNAPSHOT.jar"
- This is a spring boot application and it starts an embedded server on 8080

#Assumptions
- No validation for received transaction is done. App assumes that "amount" is valid double number (positive/negative).
It also assumes that timestamp is a valid long and is in not in the future.
- It takes current second and last 59 seconds (60 seconds time window) for transaction insertion and statistics calculation.
- For static calculations it truncates to seconds. This means its precision doesn't go for milliseconds.
- If no record found for last 60 seconds, "statistics" endpoint returns 0 for all fields.

#Complexity
- Currently time and memory complexity of both APIs is O(1).

#General algorithm
- It keeps a map with 60 entries for 60 seconds of statistics time window. Each keeps at most one record.
- For inserting a transaction it finds the proper place for this transaction according to its timestamp.
Then it updates existing one, if it's still valid.
Otherwise (meaning the record is older than 60 seconds or there is not record in that place yet) it replaces current place with given transaction.
- For getting statistics, it iterates over at most 60 entries of map and if that entry is not stale (meaning the record exist and it is younger than 60 seconds)
it will add up the statistics.

#Concurrency
- Requests to insert a transaction are synchronised using a lock.
- Request to get statistics are not needed to get synchronised as at each point of the time they get a consistent view of transactions.

#Misc
- Currently algorithm is implemented using map. But it could as well be implemented using a simple array.
- "TransactionService" creates short-lived object of type "TransactionProcessor" and "StatisticsProcessor" to serve requests.
If the load on the system is high, this leads to a lot of young generation GCs which could cause the performance.
To amend, if it's an issue, one can simply move their logic to "TransactionService" which decreases readability a little bit.
 