Ratelimiter project is RESTAPI throttling component. It is a test assignment created for BlueOptima by me (Nikhil Kumar Summi ).

Setting up it on Eclipse.
1) Import this project as a existing maven project into eclipse env.
2) update all the dependency by selecting same option via Eclipse->Maven options.

Technical assumption and main points
1) JDK version should be equal to or greater 1.8 
2) It is spring boot application.
3) For API throttling via Rate Limiting I am going with a persistence logic of In-Memory Redis. 
	It can be extended to include other options like In-Memory DataStruture, DB persistence etc.
4) This component is auto stater by using AutoConfiguration approach for Spring Boot components.
5) To defined new Rest API mapping, User API throttling rule please check and update application.yml
6) Test folder include the requried possible test cases asked in task. Please refer the RedisApplicationTestIT.java for the test cases.
7) RESTAPI has been defined in RedisApplication.java class.


Mapping :- At present I defined two mapping for URL those are given below. In case you want to defined new you have to 
provide a name (Map-Key) it should be unique and value should include an id, path (the RestAPI path) and url (after processing
where to send request)

    mapping-map:
      developers:
        id: api_v1_developers
        path: /api/v1/developers
        url: forward:/
      organizations:
        id: api_v1_organizations
        path: /api/v1/organizations
        url: forward:/
        

API throttling rules: It is map of rule where the key is the URLMapping.ID (that is defined in Mapping-map.value).
Its value is a list of Rule in that each Rule define the Limit(number of time it can be called) refresh-after time after
that it need to refreshed and RuleType (that is List). 
RuleType talks about the specifi Rule we need to apply in this case we have USER RuleType.

Example below 
-> api_v1_developers is a Key of RuleMap that is for RestAPI path /api/v1/developers
	it has two Rules defined
		1) Rule has a limit of 3 and refresh-after time is 60 seconds and it is application for user1

    rule-map:
      api_v1_developers:
        - rule-limit: 3
          refresh-after: 60
          type:
            - user=user1
        - rule-limit: 2
          refresh-after: 60
          type:
            - user=user2
            
            
Default Rule:- DefaulRule defined below it is has a limit of 5 and refreshe-after 1 second configured for user=UNKNOWN

    default-rule:
      - rule-limit: 5
        refresh-after: 1
        type:
           - user=unknown
           

Test: Please refer class RedisApplicationTest. It include all required test cases asked in assignment.
1) In case you are planning to add more or update existing test case pleas do consider the Request Headers for User information
2) Please do refer the RedisApplication for RestAPI calls available.
3) Please do refer the YML rule for limit of API calls.