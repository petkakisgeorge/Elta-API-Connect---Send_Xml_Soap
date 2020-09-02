import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Send_Soap_Xml{
    private static String[] Data = new String[8];
    private static String UserName; // Ονοματεπώνυμο
    private static String Address; // Διεύθυνση
    private static String Area; // Περιοχή
    private static String Tk; // Ταχυδρομικός κώδικας
    private static String Cellphone_Number; // Κινητό
    private static String Weight; // Βάρος Αποστολής
    private static String Tem; // Αριθμός Τεμαχίων
    private static String Ant; // Ποσό Αντικαταβολής

    public static void main(String args[]) {
        Data[7] = "";
        Ant = Data[7];
        try
        {
            FileInputStream fis=new FileInputStream("input.txt");
            Scanner sc=new Scanner(fis);
            int i=0;
            while(sc.hasNextLine())
            {
                Data[i] = sc.nextLine();
                i++;
            }
            sc.close();     //closes the scanner
            UserName = Data[0]; // Ονοματεπώνυμο
            Address = Data[1]; // Διεύθυνση
            Area = Data[2]; // Περιοχή
            Tk = Data[3]; // Ταχυδρομικός κώδικας
            Cellphone_Number = Data[4]; // Κινητό
            Weight = Data[5]; // Βάρος Αποστολής
            Tem = Data[6]; // Αριθμός Τεμαχίων
            Ant = Data[7]; // Ποσό Αντικαταβολής
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    String soapEndpointUrl = "http://212.205.47.226:9003";
        String soapAction = "/CREATEAWB/CREATEAWB/READRequest";

        callSoapWebService(soapEndpointUrl, soapAction);
    }

    private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

            // Print the SOAP Response
            //System.out.println("Response SOAP Message:");
            //soapResponse.writeTo(System.out);
            String t = soapMessageToString(soapResponse);
            String tt =getTagValue(t, "ns0:vg_code");
            System.out.println(tt);
            try {
                FileWriter myWriter = new FileWriter("OneVoucher.txt");
                myWriter.write(tt);
                myWriter.close();
                //System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }

    }

    private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("cre", "/CREATEAWB");
        SOAPBody soapBody = envelope.getBody();
        SOAPElement element = soapBody.addChildElement("READ", "cre");
        SOAPElement modelElement = element.addChildElement("pel_user_code", "cre"); // 	Κωδικός Χρήστη
        modelElement.setValue("9999999");
        SOAPElement modelElement2 = element.addChildElement("pel_user_pass", "cre"); // Κωδικός Ασφαλείας
        modelElement2.setValue("9999999");
        SOAPElement modelElement3 = element.addChildElement("pel_apost_code", "cre"); // Κεφαλικός κωδικός Αποστολέα
        modelElement3.setValue("999999999");

        SOAPElement modelElement4 = element.addChildElement("pel_paral_name", "cre"); // Ονοματεπώνυμο
        modelElement4.setValue(UserName);
        SOAPElement modelElement5 = element.addChildElement("pel_paral_address", "cre"); // Διεύθυνση
        modelElement5.setValue(Address);
        SOAPElement modelElement6 = element.addChildElement("pel_paral_area", "cre"); // Περιοχή
        modelElement6.setValue(Area);
        SOAPElement modelElement7 = element.addChildElement("pel_paral_tk", "cre"); // Ταχυδρομικός κώδικας
        modelElement7.setValue(Tk);
        SOAPElement modelElement8 = element.addChildElement("pel_paral_thl_1", "cre"); // Κινητό
        modelElement8.setValue(Cellphone_Number);
        SOAPElement modelElement9 = element.addChildElement("pel_paral_thl_2", "cre");
        SOAPElement modelElement10 = element.addChildElement("pel_baros", "cre"); // Βάρος Αποστολής
        modelElement10.setValue(Weight);
        SOAPElement modelElement11 = element.addChildElement("pel_temaxia", "cre"); // Αριθμός Τεμαχίων
        modelElement11.setValue(Tem);
        if(!Ant.equals("")) {
            SOAPElement modelElement12 = element.addChildElement("pel_ant_poso", "cre"); // Ποσό Αντικαταβολής
            modelElement12.setValue(Ant);
        }



        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();
        return soapMessage;
    }


    public static String soapMessageToString(SOAPMessage message)
    {
        String result = null;

        if (message != null)
        {
            ByteArrayOutputStream baos = null;
            try
            {
                baos = new ByteArrayOutputStream();
                message.writeTo(baos);
                result = baos.toString();
            }
            catch (Exception e)
            {
            }
            finally
            {
                if (baos != null)
                {
                    try
                    {
                        baos.close();
                    }
                    catch (IOException ioe)
                    {
                    }
                }
            }
        }
        return result;
    }

    public static String getTagValue(String xml, String tagName){
        return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
    }

}