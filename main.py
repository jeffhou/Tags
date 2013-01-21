import os
import time#sleep() sleeps for x seconds
def read_file_lines(file_name):
	file=open(file_name)
	file_lines=file.readlines()
	file.close()
	return file_lines
def write_file_lines(file_name,file_lines):
	file=open(file_name,"w")
	for i in file_lines:
		print(str(i).strip(),file=file)
	file.close()
def get_old_list():
	contents_file=open("contents.txt")
	old_list=contents_file.readlines()
	contents_file.close()
	for i in range(len(old_list)):
		old_list[i]=old_list[i].strip()
	return old_list
def get_new_list():
	new_list=os.listdir()
	new_list.pop(new_list.index("contents.txt"))
	new_list.pop(new_list.index("tags.txt"))
	new_list.pop(new_list.index("identifiers.txt"))
	new_list.pop(new_list.index("counter.txt")) # counts which file should be added next
	return new_list
def update_removed(removed_file_name):
	update_removed_counter(removed_file_name)
	update_removed_identifiers(removed_file_name)
def update_removed_counter(removed_file_name):
	counter_file=open("counter.txt")
	counter_lines=counter_file.readlines()
	counter_file.close()
	counter_lines.append(removed_file_name)
	counter_file=open("counter.txt","w")
	for i in counter_lines:
		if i!="\n":
			print(i.strip(),file=counter_file)
	counter_file.close()
def update_removed_identifiers(removed_file_name):
	identifiers_file=open("identifiers.txt")
	identifiers_lines=identifiers_file.readlines()
	identifiers_file.close()
	counter_i=0
	while counter_i<len(identifiers_lines):
		if identifiers_lines[counter_i].startswith(removed_file_name+",,"):
			identifiers_lines.pop(counter_i)
		else:
			counter_i+=1
	identifiers_file=open("identifiers.txt","w")
	for i in identifiers_lines:
		if i!="\n":
			print(i.strip(),file=identifiers_file)
	identifiers_file.close()
def SHOUT(message):
	if VERBOSE:
		print(message)
VERBOSE=False
os.chdir("SampleFolder")

while True: #main loop
	SHOUT("GETTING OLD LIST OF FILES IN FOLDER FROM CONTENTS.TXT")
	old_list=get_old_list()
	SHOUT("GETTING NEW LIST OF FILES FROM OS.LISTDIR()")
	new_list=get_new_list()
	if old_list!=new_list:
		SHOUT("SOMETHING IS DIFFERENT!")
		
		for i in old_list:
			SHOUT("CHECKING TO SEE IF FILE " + i + " IS STILL IN THE FOLDER...")
			if i not in new_list:
				SHOUT("FILE " + i +" NO LONGER EXISTS!")
				update_removed(i)
		identifiers_lines=read_file_lines("identifiers.txt")
		
		SHOUT("OPENING IDENTIFIERS")
		for i in range(len(identifiers_lines)):
			identifiers_lines[i]=identifiers_lines[i].split(",,")
		while([] in identifiers_lines):
			identifiers_lines.pop(identifiers_lines.index([]))
		
		
		for i in range(len(new_list)):
			SHOUT("CHECKING TO SEE IF FILE " + new_list[i] +" USED TO BE IN THE FOLDER...")
			if new_list[i] not in old_list:
				SHOUT("NEW FILE "+new_list[i]+" WAS ADDED!")
				SHOUT("UPDATING COUNTER.TXT AND IDENTIFIERS.TXT...")
				old_file_name=new_list[i]
				counter_lines=read_file_lines("counter.txt")
				for j in range(len(counter_lines)):
					counter_lines[j]=int(counter_lines[j].strip())
				if(len(counter_lines)==1):
					new_file_name=str(counter_lines[0])
					counter_lines[0]+=1
				else:
					new_file_name=str(counter_lines.pop(1))
				identifiers_lines.append([new_file_name,old_file_name])
				write_file_lines("counter.txt",counter_lines)
				SHOUT("RENAMING INSERTED FILE...")
				os.rename(old_file_name,new_file_name)
				new_list[i]=new_file_name
		SHOUT("UPDATING CONTENTS.TXT...")
		write_file_lines("contents.txt",new_list)
		SHOUT("UPDATING IDENTIFIERS.TXT...")
		identifiers_file=open("identifiers.txt","w")
		for i in identifiers_lines:
			j=",,".join(i)			
			if j.strip()!="":
				print(j.strip(),file=identifiers_file)
		identifiers_file.close()
		SHOUT("DONE WITH ONE LOOP")
	SHOUT("SLEEPING FOR A SECOND...")
	time.sleep(1)
