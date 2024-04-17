package tweeneditor;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Slider;
import controlP5.Textlabel;
import iat265.xha98.MechanismFactory;
import iat265.xha98.Scrubbable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import processing.core.PApplet;
import processing.core.PFont;

/**
 *
 * @author aga53
 */
public class Sketch extends PApplet {

    final int PLAY_BTN = 10;
    final int STOP_BTN = 11;
    final int ADD_CHNL_BTN = 12;

    final int PROPERTIES_DROPDOWN = 13;

    final int ROTATION_SLIDER = 14;
    final int SLIDER_R = 15;
    final int SLIDER_G = 16;
    final int SLIDER_B = 17;

    final int SAVE_BTN = 18;
    final int LOAD_BTN = 19;

    final int ADD_KF = 20;

    final int PAUSE_BTN = 21;

    Scrubber scrubber;
    ControlP5 gui;
    Scrubbable selected;
    List<Scrubbable> scrubbables;

    Button playBtn;
    Textlabel selectedLabel;
    DropdownList propertyDropdown;
    Button addChannelButton;
    Slider rotationSlider, radiusSlider, colorSlider, rotationSpeedSlider, sliderR, sliderG, sliderB;

    long lastUpdatedAt;
    boolean play;
    private Button saveBtn;

    private Textlabel errorLabel;
    private Textlabel timeLabel;
    private boolean paused;

    @Override
    public void settings() {
        size(displayWidth, displayHeight);
    }

    @Override
    public void setup() {
        play = false;

        // initialize the GUI
        gui = new ControlP5(this);
        playBtn = gui.addButton("Play").setId(PLAY_BTN).setPosition(10, 20);
        Button pauseBtn = gui.addButton("Pause")
                .setId(PAUSE_BTN)
                .setPosition(playBtn.getPosition()[0] + playBtn.getWidth() + 10, 20);
        gui.addButton("Stop")
                .setId(STOP_BTN)
                .setPosition(pauseBtn.getPosition()[0] + pauseBtn.getWidth() + 10, 20);

        saveBtn = gui.addButton("Save").setId(SAVE_BTN).setPosition(playBtn.getPosition()[0],
                playBtn.getPosition()[1] + playBtn.getHeight() + 30);
        gui.addButton("Load").setId(LOAD_BTN).setPosition(saveBtn.getWidth() + 30, saveBtn.getPosition()[1]);

        PFont pfont = createFont("Arial", 20, true); // use true/false for smooth/no-smooth        

        // for every possible property in the animal create sliders
        // create sliders for radius, rotationSpeed, and color
        radiusSlider = setupSlider("radius").setRange(0, 100).setId(22); // assign new IDs for each slider
        rotationSpeedSlider = setupSlider("rotationSpeed").setRange(0, 10).setId(23);
        colorSlider = setupSlider("color").setRange(0, 255).setId(24);

// by default, hide all the sliders
        radiusSlider.setVisible(false);
        rotationSpeedSlider.setVisible(false);
        colorSlider.setVisible(false);


        selectedLabel = gui.addLabel("selected")
                .setPosition(50,
                        radiusSlider.getPosition()[1] + radiusSlider.getHeight() + 20)
                .setHeight(30)
                .setColor(color(0))
                .setFont(pfont);
        propertyDropdown = gui.addDropdownList("properties")
                .setId(PROPERTIES_DROPDOWN)
                .setPosition(selectedLabel.getPosition()[0],
                        selectedLabel.getPosition()[1] + selectedLabel.getHeight() + 5)
                .setBarHeight(20);
        addChannelButton = gui.addButton("Add Channel")
                .setId(ADD_CHNL_BTN)
                .setPosition(propertyDropdown.getPosition()[0] + propertyDropdown.getWidth() + 5,
                        propertyDropdown.getPosition()[1]);

        Button addKFBtn = gui.addButton("Add key frame")
                .setId(ADD_KF)
                .setPosition(saveBtn.getPosition()[0], propertyDropdown.getPosition()[1] + propertyDropdown.getHeight() + 5);

        timeLabel = gui.addLabel("timeLabel")
                .setPosition(addKFBtn.getPosition()[0], addKFBtn.getPosition()[1] + addKFBtn.getHeight() + 5)
                .setColor(color(128)).setValue("");

        errorLabel = gui.addLabel("")
                .setColor(color(255, 0, 0))
                .setFont(createFont("Georgia", 12))
                .setPosition(timeLabel.getPosition()[0], timeLabel.getPosition()[1] + timeLabel.getHeight() + 5).setValue("");

        // get animal and update its graphics
        Scrubbable animal = getAnimal();

        /*
         create a list of every component in the animal. This list is required 
         by the Scrubber to load animation
         */
        scrubbables = new ArrayList<>();
        Iterator<Scrubbable> it = animal.createIterator();
        while (it.hasNext()) {
            scrubbables.add(it.next());
        }

        // create a scrubber. The scrubber holds all the channels.
        scrubber = new Scrubber(200, height / 2, width, height, scrubbables);
        scrubber.g = this.g;

        /*
         uncomment the two lines below if you want to add some default channels 
         and keyframes. Only use for debugging purpose.
         */
        //ScrubberChannel ch = scrubber.addChannel(animal, "rotation");
        //ch.addkFrame(new KeyFrame(0, animal.getParameter("rotation")));
    }

    /*
     A helper function to create a sliders
     */
    private Slider setupSlider(String name) {
        Slider slider = gui.addSlider(name);
        slider.setColorCaptionLabel(color(0)).setPosition(saveBtn.getPosition()[0], saveBtn.getPosition()[1] + saveBtn.getHeight() + 30).setWidth(175);
        return slider;
    }

    /*
     This methods is called when controlP5 events are triggered
     */
    public void controlEvent(ControlEvent event) {


        System.out.println(event.getId());

        switch (event.getId()) {


            case PLAY_BTN:
                lastUpdatedAt = millis();
                play = true;
                paused = false;
                break;

            case STOP_BTN:
                play = false;
                paused = false;
                scrubber.setCurrentT(0);
                scrubber.reset();
                break;

            case PAUSE_BTN:
                play = false;
                paused = true;
                break;

            case SAVE_BTN:
                scrubber.saveAnimation("animation.xml");
                break;

            case LOAD_BTN:
                scrubber.loadAnimation("animation.xml");
                break;

            case ADD_CHNL_BTN:
                if (selected != null) {
                    int selectedIndex = (int) propertyDropdown.getValue();
                    Map<String, Object> item = propertyDropdown.getItem(selectedIndex);
                    String stringValue = (String) item.get("name");
                    ScrubberChannel channel = scrubber.addChannel(selected, stringValue);
                    channel.addkFrame(new KeyFrame(0, selected.getParameter(stringValue)));
                } else {
                    println("Nothing selected");
                }
                break;

            case PROPERTIES_DROPDOWN:
                int selectedIndex = (int) propertyDropdown.getValue();
                Map<String, Object> item = propertyDropdown.getItem(selectedIndex);
                String stringValue = (String) item.get("name");

                // Hide all sliders first
                radiusSlider.setVisible(false);
                rotationSpeedSlider.setVisible(false);
                colorSlider.setVisible(false);

                // Show the appropriate slider based on the selected property
                switch (stringValue) {
                    case "radius":
                        radiusSlider.setVisible(true);
                        radiusSlider.setValue(selected.getParameter("radius"));
                        break;
                    case "rotationSpeed":
                        rotationSpeedSlider.setVisible(true);
                        rotationSpeedSlider.setValue(selected.getParameter("rotationSpeed"));
                        break;
                    case "color":
                        colorSlider.setVisible(true);
                        colorSlider.setValue(selected.getParameter("color"));
                        break;
                }
                break;

            case 22: // Radius slider ID
                if (selected != null) {
                    selected.setParameter("radius", (int) radiusSlider.getValue());
                }
                break;

            case 23: // Rotation speed slider ID
                if (selected != null) {
                    selected.setParameter("rotationSpeed", (int) rotationSpeedSlider.getValue());
                }
                break;

            case 24: // Color slider ID
                if (selected != null) {
                    selected.setParameter("color", (int) colorSlider.getValue());
                }
                break;


            case ROTATION_SLIDER:

                break;

            case SLIDER_R:

                break;

            case SLIDER_G:
                // No need for this case as we're only dealing with a single color value
                break;

            case SLIDER_B:
                // No need for this case as we're only dealing with a single color value
                break;


            case ADD_KF:
                int index = (int) propertyDropdown.getValue();
                if (propertyDropdown.getItems().size() > 0) {
                    final Map<String, Object> item1 = propertyDropdown.getItem(index);
                    String prop = (String) item1.get("name");
                    errorLabel.setValue("");
                    if (selected != null && prop != null) {
                        KeyFrame f = new KeyFrame(scrubber.getCurrentT(), selected.getParameter(prop));
                        ScrubberChannel channel = scrubber.findChannel(selected, prop);
                        if (channel != null) {
                            channel.addkFrame(f);
                        } else {
                            errorLabel.setValue("Could not find a channel for \ncomponent:" + selected.getName() + " and property: " + prop);
                        }
                    } else {
                        errorLabel.setValue("Please select a component and a property");
                    }
                } else {
                    errorLabel.setValue("Please select a component and a property");
                }
                break;
        }
    }

    @Override
    public void draw() {
        background(255);

        timeLabel.setValue("time: " + scrubber.getCurrentT() + " ms");

        noFill();
        //draw panel borders: menu, scrubber, and editable area
        rect(0, 0, 250, height / 2 - 5); // menu
        line(0, height / 2 - 5, width, height / 2 - 5);
        if (play) {
            long t = scrubber.getCurrentT() + millis() - lastUpdatedAt;
            lastUpdatedAt = millis();
            scrubber.setCurrentT(t);
        }
        // the scrubber should be drawn when the animation is playing or not
        scrubber.draw();

        for (Scrubbable scrubbable : scrubbables) {
            scrubbable.draw();
        }
    }

    @Override
    public void mousePressed() {
        //clicking scrubber region
        if (mouseY > height / 2) {
            ScrubberChannel channel = scrubber.mousePressed(selected, mouseX, mouseY);
            String property = null;
            if (channel != null) {
                selected = channel.getTarget();
                property = channel.getProperty();
            } else {
                selected = null;
            }
            updateForSelected(selected);
            updatePropertyDropdown(selected, property);
        } else {
            //mouseY >= height/2
            if (mouseX > 250) {
                selected = null;
                for (Scrubbable sb : scrubbables) {
                    Scrubbable pick = sb.pick(mouseX, mouseY);
                    if (pick != null) {
                        selected = pick;
                        break;
                    }
                }
                updateForSelected(selected);
            }else{
                //clicking in the edit region
            }
        }
        
    }

    private void updateForSelected(Scrubbable s) {
        if (selected != null) {
            String[] properties = selected.getProperties();
            selectedLabel.setValue(selected.getName());
            propertyDropdown.clear();
            propertyDropdown.addItems(properties);
            propertyDropdown.open();
            propertyDropdown.setValue(0);
        } else {
            selectedLabel.setValue("No Selection");
            propertyDropdown.clear();
        }
    }

    private void updatePropertyDropdown(Scrubbable selected, String property) {
        if (selected != null && property != null) {
            String[] properties = selected.getProperties();
            int index = -1;
            for (int i = 0; i < properties.length; i++) {
                if (properties[i].equals(property)) {
                    index = i;
                }
            }
            propertyDropdown.setValue(index);
        } else {

        }
    }

    private Scrubbable getAnimal() {
        Reflections conf = new Reflections("iat265");
        Set<Class<? extends MechanismFactory>> factories = conf.getSubTypesOf(MechanismFactory.class);
        if (factories.size() != 1) {
            System.err.println("You must have exactly one class which implements the CreatureFactory interface");
            System.exit(-1);
        }
        Scrubbable creature = null;
        try {
            MechanismFactory factory = factories.iterator().next().newInstance();
            creature = factory.getMechanism(this);
        } catch (InstantiationException ex) {
            System.err.println("Please make sure that your factory class does not have a constructor with 1 or more arguments.");
            System.exit(-1);
        } catch (IllegalAccessException ex) {
            System.err.println("Please make sure that the factory constructor (if you have one) is public.");
            System.exit(-1);
        }
        if (creature == null) {
            System.err.println("Your creature factory getCreature() method is returning a null value");
        }
        return creature;
    }
}
