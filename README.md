# cs122b-spring18-team-32
performance tests are ran on the fulltext search in the home page of site. (ip/api/fulltext)

script is in main folder of project
script is written in python
script reads through entire log file and adds to number of requests made as well as time spent on TS / TJ
afterwards, script prints total queries, time spent on TS/TJ, and average time spent on TS/TJ (total time / number of queries)

Log files of query time and servlet time is saved as timeLog.txt in each aws instance
servlet writes to log file in the following format: [type] [time], ie: elapsedTimeTS 123456
combined results of each logfile is saved in main project folder as 122bLogFile.txt

html file and pictures are saved in folder called html_files that can be found in main project folder
