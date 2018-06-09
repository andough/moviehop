
# coding: utf-8

# In[15]:


tomcat_log = open("/home/ubuntu/timeLog.txt","r")
timeTJ = 0
timeTS = 0
for c, line in enumerate(tomcat_log,1):
    words = line.split()
    #print(line.split())
    if words[0] == "elapsedTimeTJ" : 
        timeTJ += int(words[1])
    if words[0] == "elapsedTimeTS" : 
        timeTS += int(words[1])

print("query count: " + str(c/2))
print("timeTS: " + str(timeTS * 10**-9))
print("average TS: " + str(timeTS * 10**-9 *2 / c))
print("timeTJ: " + str(timeTJ * 10**-9))
print("average TJ: " + str(timeTJ * 10**-9 *2 / c))

