# TripleLift
TripleLift Server Challenge
================================================================================

- The class name for the Solution is TripleLiftServer.
- Its function fetchData takes a long array as input and outputs a string.
- The string can be outputted to get the relevant data.
- I have written four test cases to check basic input conditions.

The assumptions I made during writing this - 
- The timeout per advertiser ID and not per call of function fetchData.
- No external libraries allowed, hence my own classes for JSON parser.
- Had the libraries been allowed, would have used:
  -- Apache HttpComponents Library for connecting to URL
  -- Would have used org.json or Jackson library for parsing JSON.
- Data in the JSON format does not have anomalies and maintains integrity.
