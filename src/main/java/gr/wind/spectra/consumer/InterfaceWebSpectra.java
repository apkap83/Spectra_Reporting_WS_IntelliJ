
package gr.wind.spectra.consumer;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "InterfaceWebSpectra", targetNamespace = "http://web.spectra.wind.gr/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface InterfaceWebSpectra {


    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.GetHierarchyResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod
    @WebResult(name = "getHierarchyResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/getHierarchyRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/getHierarchyResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/getHierarchy/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/getHierarchy/Fault/InvalidInputException")
    })
    public GetHierarchyResponse getHierarchy(
        @WebParam(name = "getHierarchy", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        GetHierarchy parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.SubmitOutageResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod
    @WebResult(name = "submitOutageResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/submitOutageRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/submitOutageResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/submitOutage/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/submitOutage/Fault/InvalidInputException")
    })
    public SubmitOutageResponse submitOutage(
        @WebParam(name = "submitOutage", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        SubmitOutage parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.GetOutageStatusResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod
    @WebResult(name = "getOutageStatusResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/getOutageStatusRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/getOutageStatusResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/getOutageStatus/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/getOutageStatus/Fault/InvalidInputException")
    })
    public GetOutageStatusResponse getOutageStatus(
        @WebParam(name = "getOutageStatus", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        GetOutageStatus parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.ModifyOutageResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod
    @WebResult(name = "modifyOutageResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/modifyOutageRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/modifyOutageResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/modifyOutage/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/modifyOutage/Fault/InvalidInputException")
    })
    public ModifyOutageResponse modifyOutage(
        @WebParam(name = "modifyOutage", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        ModifyOutage parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.CloseOutageResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod
    @WebResult(name = "closeOutageResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/closeOutageRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/closeOutageResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/closeOutage/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/closeOutage/Fault/InvalidInputException")
    })
    public CloseOutageResponse closeOutage(
        @WebParam(name = "closeOutage", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        CloseOutage parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

    /**
     * 
     * @param password
     * @param userName
     * @param parameters
     * @return
     *     returns gr.wind.spectra.consumer.NLUActiveResponse
     * @throws Exception_Exception
     * @throws InvalidInputException_Exception
     */
    @WebMethod(operationName = "NLU_Active")
    @WebResult(name = "NLU_ActiveResponse", targetNamespace = "http://web.spectra.wind.gr/", partName = "result")
    @Action(input = "http://web.spectra.wind.gr/InterfaceWebSpectra/NLU_ActiveRequest", output = "http://web.spectra.wind.gr/InterfaceWebSpectra/NLU_ActiveResponse", fault = {
        @FaultAction(className = Exception_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/NLU_Active/Fault/Exception"),
        @FaultAction(className = InvalidInputException_Exception.class, value = "http://web.spectra.wind.gr/InterfaceWebSpectra/NLU_Active/Fault/InvalidInputException")
    })
    public NLUActiveResponse nluActive(
        @WebParam(name = "NLU_Active", targetNamespace = "http://web.spectra.wind.gr/", partName = "parameters")
        NLUActive parameters,
        @WebParam(name = "UserName", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "UserName")
        String userName,
        @WebParam(name = "Password", targetNamespace = "http://web.spectra.wind.gr/", header = true, partName = "Password")
        String password)
        throws Exception_Exception, InvalidInputException_Exception
    ;

}
