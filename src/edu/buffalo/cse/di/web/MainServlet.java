package edu.buffalo.cse.di.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import edu.buffalo.cse.di.apis.GoogleProductSearch;
import edu.buffalo.cse.di.util.entity.Node;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet(description = "Main Servlet", urlPatterns = { "/MainServlet" })
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String input = request.getParameter("entities");
		List<String> entities = Arrays.asList(input.split("\n"));
		System.out.println(entities.size());
		System.out.println(entities);
		List<List<Node>> clusters = GoogleProductSearch.performEntityResolution(entities);
		StringBuilder values = new StringBuilder();
		
		JSONObject obj = new JSONObject();
		
		
		
		System.out.println("");
		for(int j=0; j< clusters.size(); j++ ) {
			if(j != 0) {
				values.append(",");
			}
			JSONArray array = new JSONArray();
			List<Node> cluster = clusters.get(j);
			StringBuilder value = new StringBuilder();
			for(int i=0; i<cluster.size(); i++) {
				if(i != 0) {
					value.append(",");
				}
				array.add(cluster.get(i).getString());
				value = value.append('"').append(cluster.get(i).toString()).append('"'); 
			}
			//values = values.append('"').append(value.toString()).append('"');
			obj.put("cluster-"+(j+1), array);
			values = values.append(value.toString());
		}
		//System.out.println(obj.toString());
		System.out.println("--------------");
		//System.out.println(values);
		response.getWriter().write(obj.toString());
	}
	
	public String getSampleJson() {
		String sample = "{\"cluster-1\": [\"item1\",\"item2\"],\"cluster-2\": [\"item3\",\"item4\"],\"cluster-3\": [\"item1\",\"item2\"]}";
		return sample;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
