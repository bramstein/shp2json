package com.bramstein.shp2json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
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
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

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
		//File file = new File("/home/bs/Desktop/countries/countries_simpl.shp");
		//File file = new File("/home/bs/Desktop/countries/Azimuth_LoRes_Nations.shp");
        if (file == null) {
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource featureSource = store.getFeatureSource();
        FeatureCollection collection = featureSource.getFeatures();
        
        CoordinateReferenceSystem source = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem target = CRS.decode("EPSG:23032");
       
        MathTransform transform = CRS.findMathTransform(source, target);
      // CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null);
      // Set<String> authorityCodes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);

       //System.out.println(authorityCodes);
    //    CoordinateReferenceSystem sphericalMercator = CRS.decode("EPSG:9804");
        /*
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:23032");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);

        */
   
      
 //     featureSource.get
      
        System.out.println(featureSource.getBounds().transform(target, true));
        
        Iterator i = collection.iterator();
        
        JSONArray a = new JSONArray();
        
    //    System.out.println(collection.getBounds().);
        
        while(i.hasNext()) {
    		Feature f = (Feature) i.next();
    		
        	JSONObject o = new JSONObject();
        	for (Property p : f.getProperties()) {            		
        		if (p.getType() instanceof GeometryType) {
        			
        		
        			
        			MultiPolygon g = (MultiPolygon) p.getValue();
        		
        			com.vividsolutions.jts.geom.Geometry l = JTS.transform(g, transform);
        		//	System.out.println(l);
        			JSONArray coords = new JSONArray();
        			for (Coordinate c : l.getCoordinates()) {
        				JSONArray point = new JSONArray();
        			//	transform.
        				point.add(truncate(c.x, 1));
        				point.add(truncate(c.y, 1));
        				coords.add(point);
        			}
        			o.element(p.getName().toString(), coords);
        		} else {
        			if (p.getValue() != null && !p.getValue().toString().isEmpty()) {
        			//	if (p.getName().toString().equals("Name1")) {
        					o.element(p.getName().toString(), p.getValue());
        			//	}
        			}
        		}
        	}
        	a.add(o);
        }
        
        FileWriter fstream = new FileWriter("/home/bs/Desktop/countries/out.json");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("var data = ");
        out.write(a.toString());
        out.write(";");
        out.close();
    }
}
