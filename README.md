# origin-url-shortner
A code challenge for Origin job


why choose concurrentHashmap?
1. in memory store 
2. thread safe for concurrent access
3. no need to mannufally synchronise metholds in many cases


when save 2 to hash maps not guarentee atomicity:

The in-memory repository uses thread-safe maps for concurrent access. For a real production system, I would consider stronger atomicity guarantees around multi-index updates depending on consistency needs.