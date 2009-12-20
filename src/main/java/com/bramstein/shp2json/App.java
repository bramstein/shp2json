package com.bramstein.shp2json;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
//import org.geotools.map.DefaultMapContext;
//import org.geotools.map.MapContext;
//import org.geotools.swing.JMapFrame;
//import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.GeometryType;
import org.opengis.geometry.Geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;


/**
 * Hello world!
 *
 */
public class App 
{
	public static double truncate(double x, int precision) {
		double t = Math.pow(10, precision);
		return Math.round(x * t) / t;
	}
	
	public static void main( String[] args ) throws Exception
    {
        // display a data store file chooser dialog for shapefiles
      //  File file = JFileDataStoreChooser.showOpenFile("shp", null);
		File file = new File("/home/bs/Desktop/countries/110m-admin-0-countries.shp");
        if (file == null) {
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource featureSource = store.getFeatureSource();
        FeatureCollection collection = featureSource.getFeatures();
  
        Iterator i = collection.iterator();
        
        JSONArray a = new JSONArray();
        
    //    System.out.println(collection.getBounds().);
        
        while(i.hasNext()) {
    		Feature f = (Feature) i.next();

        	JSONObject o = new JSONObject();
        	for (Property p : f.getProperties()) {            		
        		if (p.getType() instanceof GeometryType) {
        			MultiPolygon g = (MultiPolygon) p.getValue();
        			JSONArray coords = new JSONArray();
        			for (Coordinate c : g.getCoordinates()) {
        				JSONArray point = new JSONArray();
        				point.add(truncate(c.x, 0));
        				point.add(truncate(c.y, 0));
        				coords.add(point);
        			}
        			o.element(p.getName().toString(), coords);
        		} else {
        			if (p.getValue() != null && !p.getValue().toString().isEmpty()) {
        				if (p.getName().toString().equals("Name1")) {
        					o.element(p.getName().toString(), p.getValue());
        				}
        			}
        		}
        	}
        	a.add(o);
        }
       
        System.out.println(a);
    }
}
