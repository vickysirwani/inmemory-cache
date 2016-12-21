InMemory Cache
===============


Basic useful feature list:

 * InMemory Server and a CLI that supports:
	1. Set a key
    2. Get a key
    3. Delete a key
    4. Count the number of times a value occurs in the cache
    5. Begin a transaction
    6. Rollback a transaction
    7. Commit a transaction
 


Setup
---
####For Mac:
    brew install gradle
    

* You will have to run the server and the client separately.
* To run:
	* Server:
	 	*  `cd cache`  
	 	*  `./gradlew build`      
        *  `./gradlew execute`      
	
    * Client:
	 	*  `cd cache-client`
		*  `./gradlew build`
		*  `./gradlew --console plain execute`


Usage:
------
*  **Once you run the client:**
	* Set a key: `SET A B`
    * Get a key: `GET A`
    * Delete a key: `DELETE A`
    * Count the number of occurences of value B: `COUNT B`
    * Start a transaction: `BEGIN`
    * Commit a transaction: `COMMIT`
    * Rollback a transaction: `ROLLBACK`

* PS: Count will give inconsistent results when run in between a transaction. Will update after fixing it.