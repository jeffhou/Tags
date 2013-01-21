import time,shutil,os
if os.path.exists('SampleFolder'):
	shutil.rmtree('SampleFolder')
os.makedirs('SampleFolder')
contents=open("SampleFolder/contents.txt","w")
print(end="",file=contents)
contents.close()

counter=open("SampleFolder/counter.txt","w")
print(str(1),end="",file=counter)
counter.close()

tags=open("SampleFolder/tags.txt","w")
print(end="",file=tags)
tags.close()

identifiers=open("SampleFolder/identifiers.txt","w")
print(end="",file=identifiers)
identifiers.close()

for i in range(10):
	new_file=open(str(i)+".z","w")
	new_file.close()