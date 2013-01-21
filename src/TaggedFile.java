import java.util.HashSet;


public class TaggedFile implements Comparable<TaggedFile>{
	
	private int id;
	private String name;
	
	public TaggedFile(String in_name){
		StringBuilder n = new StringBuilder();
		do{
			n.append(in_name.substring(0,in_name.indexOf("(")+1));
			in_name=in_name.substring(in_name.indexOf("(")+1);
		}while(in_name.substring(in_name.indexOf("(")+1).contains("("));
		n.deleteCharAt(n.length()-1);
		name=n.toString().trim();
		in_name=in_name.substring(0,in_name.length()-1);
		id=Integer.parseInt(in_name);
		//System.out.println("id: ["+id+"]");
		//System.out.println("name: ["+name+"]");
	}
	public int compareTo(TaggedFile o) {
		return id-o.id;
	}
	public int hashCode(){
		return altToString().hashCode();
	}
	public String toString(){
		return name+" ("+id+")";
	}
	public String altToString(){
		return id + ",," +name;
	}
	public String toStringWithTags(HashSet<String> set){
		String ret = id + ",," +name;
		for(String i: set){
			ret+=",,"+i;
		}
		return ret;
	}
}
