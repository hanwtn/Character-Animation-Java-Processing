package tweeneditor;

import iat265.xha98.Scrubbable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import processing.core.PApplet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aga53
 */
public class Scrubber extends PApplet {

  private final int CHANNEL_HEIGHT = 10;
  private final int CHANNEL_MARGIN = 5;
  private final int RT_MRGN = 50;
  private final List<ScrubberChannel> channels;
  private final int x;
  private final int y;
  private long currentT;
  private final int scrubberWidth;
  final int TWEEN_TOTAL_DURATION = 15 * 1000;
  private final List<Scrubbable> allScrubbables;

  public Scrubber(int x, int y, int width, int height, List<Scrubbable> allScrubbables) {
    this.allScrubbables = allScrubbables;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    this.channels = new ArrayList<>();
    this.currentT = 0;
    this.scrubberWidth = width - x - RT_MRGN; //50 is the right margin
  }

  public void addScrubbable(Scrubbable scrubbable) {
    this.allScrubbables.add(scrubbable);
  }

  /*
    This method resets all channels.
   */
  public void reset() {
    for (ScrubberChannel channel : channels) {
      channel.reset();
    }
  }

  @Override
  public void draw() {
    for (ScrubberChannel s : channels) {
      s.draw();
    }
    pushStyle();
    stroke(255, 0, 0);
    float currentX = (scrubberWidth / (float) TWEEN_TOTAL_DURATION) * currentT + x;
    line(currentX, y, currentX, height);
    popStyle();
  }

  public ScrubberChannel addChannel(Scrubbable target, String parameter) {
    ScrubberChannel channel = new ScrubberChannel(target, parameter, x,
            channels.size() * (CHANNEL_HEIGHT + CHANNEL_MARGIN) + y,
            scrubberWidth, CHANNEL_HEIGHT,
            TWEEN_TOTAL_DURATION
    );
    channel.g = this.g;
    channels.add(channel);
//    println("adding channel for " + target.getName() + " " + parameter);
//    println(channels.size());
//    println("y = " + channels.size() * (CHANNEL_HEIGHT + CHANNEL_MARGIN) + y);
    return channel;
  }

  public void setCurrentT(long t) {
    this.currentT = t;
    for (ScrubberChannel ch : channels) {
      ch.update(currentT);
    }
  }

  public ScrubberChannel mousePressed(Scrubbable selected, int mx, int my) {
    /*
         Check if a scrubber channel is picked
     */
    for (ScrubberChannel sc : channels) {
      boolean picked = sc.pick(selected, mx, my);
      if (picked) {
        /*
                 If a scrubber channel is clicked, then update selected to 
                 channel's target
         */
        return sc;
      }
    }
    return null;
  }

  public long getCurrentT() {
    return currentT;
  }

  public void saveAnimation(String xmlFile) {
    Document dom;
    Element e = null;

    // instance of a DocumentBuilderFactory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      // use factory to get an instance of document builder
      DocumentBuilder db = dbf.newDocumentBuilder();
      // create instance of DOM
      dom = db.newDocument();

      // create the root element
      Element root = dom.createElement("scrubbers");
      for (ScrubberChannel ch : channels) {

        e = dom.createElement("scrubber");
        Element name = createTag(dom, "name", ch.getTarget().getName());
        Element property = createTag(dom, "property", ch.getProperty());

        Element frames = dom.createElement("frames");

        e.appendChild(name);
        e.appendChild(property);
        e.appendChild(frames);

        for (KeyFrame f : ch.getkFrames()) {
          Element frame = dom.createElement("frame");
          frame.setAttribute("time", f.getT() + "");
          frame.setAttribute("value", f.getValue() + "");
          frames.appendChild(frame);
        }

        root.appendChild(e);
      }

      dom.appendChild(root);

      try {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // send DOM to file
        tr.transform(new DOMSource(dom),
                new StreamResult(new FileOutputStream(xmlFile)));
      } catch (TransformerException | IOException te) {
        System.out.println(te.getMessage());
      }
    } catch (ParserConfigurationException pce) {
      System.out.println("Error trying to instantiate DocumentBuilder " + pce);
    }
  }

  private Element createTag(Document dom, String tagName, String value) throws DOMException {
    Element tag = dom.createElement(tagName);
    tag.appendChild(dom.createTextNode(value));
    return tag;
  }

  public void loadAnimation(String xmlFile) {
    List<ScrubberChannel> channels = new ArrayList<>();
    this.channels.clear();

    Document dom;
    // Make an  instance of the DocumentBuilderFactory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      // use the factory to take an instance of the document builder
      DocumentBuilder db = dbf.newDocumentBuilder();
      // parse using the builder to get the DOM mapping of the    
      // XML file
      dom = db.parse(xmlFile);

      Element doc = dom.getDocumentElement();
      NodeList scrubbers = doc.getElementsByTagName("scrubber");

      for (int i = 0; i < scrubbers.getLength(); i++) {
        Node item = scrubbers.item(i);
        NodeList channelNodes = item.getChildNodes();
        String name = null, property = null;
        List<KeyFrame> frames = null;
        for (int j = 0; j < channelNodes.getLength(); j++) {
          Node scrubberChildNode = channelNodes.item(j);
          String nodeName = scrubberChildNode.getNodeName();
          switch (nodeName) {
            case "name":
              name = scrubberChildNode.getFirstChild().getNodeValue();
              break;
            case "property":
              property = scrubberChildNode.getFirstChild().getNodeValue();
              break;
            case "frames":
              frames = extractKeyFrames(scrubberChildNode);
              break;
          }
        }
        Scrubbable target = findScrubbableTarget(name);
        ScrubberChannel channel = addChannel(target, property);
        for (KeyFrame f : frames) {
          channel.addkFrame(f);
        }
      }

    } catch (ParserConfigurationException pce) {
      System.out.println(pce.getMessage());
    } catch (SAXException se) {
      System.out.println(se.getMessage());
    } catch (IOException ioe) {
      System.err.println(ioe.getMessage());
    }
  }

  private List<KeyFrame> extractKeyFrames(Node framesNode) {
    List<KeyFrame> frames = new ArrayList<>();
    NodeList framesList = framesNode.getChildNodes();
    for (int i = 0; i < framesList.getLength(); i++) {
      Node frameNode = framesList.item(i);
      String nodeName = frameNode.getNodeName();
      if (nodeName.equals("frame")) {
        NamedNodeMap attributes = frameNode.getAttributes();
        String time = attributes.getNamedItem("time").getNodeValue();
        String value = attributes.getNamedItem("value").getNodeValue();
        KeyFrame frame = new KeyFrame(Long.parseLong(time), Float.parseFloat(value));
        frames.add(frame);
      }
    }
    return frames;
  }

  private Scrubbable findScrubbableTarget(String name) {
    for (Scrubbable scrubbable : allScrubbables) {
      if (scrubbable.getName().equals(name)) {
        return scrubbable;
      }
    }
    throw new RuntimeException(
            "No scrubbable found with name: " + name
            + ". Please check if you have added all the scrubbable components in the Scrubber");
  }

  ScrubberChannel findChannel(Scrubbable selected, String prop) {
    for (ScrubberChannel ch : channels) {
      if (ch.getTarget() == selected && ch.getProperty().equals(prop)) {
        return ch;
      }
    }
    return null;
  }
}
