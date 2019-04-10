/**
 * FileInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package weaver.docs.webservices;

public class FileInfo  implements java.io.Serializable {
    private String downloadurl;

    private Integer imagefileid;

    private String imagefilename;

    private String imagefilesize;

    public FileInfo() {
    }

    public FileInfo(
           String downloadurl,
           Integer imagefileid,
           String imagefilename,
           String imagefilesize) {
           this.downloadurl = downloadurl;
           this.imagefileid = imagefileid;
           this.imagefilename = imagefilename;
           this.imagefilesize = imagefilesize;
    }


    /**
     * Gets the downloadurl value for this FileInfo.
     * 
     * @return downloadurl
     */
    public String getDownloadurl() {
        return downloadurl;
    }


    /**
     * Sets the downloadurl value for this FileInfo.
     * 
     * @param downloadurl
     */
    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }


    /**
     * Gets the imagefileid value for this FileInfo.
     * 
     * @return imagefileid
     */
    public Integer getImagefileid() {
        return imagefileid;
    }


    /**
     * Sets the imagefileid value for this FileInfo.
     * 
     * @param imagefileid
     */
    public void setImagefileid(Integer imagefileid) {
        this.imagefileid = imagefileid;
    }


    /**
     * Gets the imagefilename value for this FileInfo.
     * 
     * @return imagefilename
     */
    public String getImagefilename() {
        return imagefilename;
    }


    /**
     * Sets the imagefilename value for this FileInfo.
     * 
     * @param imagefilename
     */
    public void setImagefilename(String imagefilename) {
        this.imagefilename = imagefilename;
    }


    /**
     * Gets the imagefilesize value for this FileInfo.
     * 
     * @return imagefilesize
     */
    public String getImagefilesize() {
        return imagefilesize;
    }


    /**
     * Sets the imagefilesize value for this FileInfo.
     * 
     * @param imagefilesize
     */
    public void setImagefilesize(String imagefilesize) {
        this.imagefilesize = imagefilesize;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof FileInfo)) return false;
        FileInfo other = (FileInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.downloadurl==null && other.getDownloadurl()==null) || 
             (this.downloadurl!=null &&
              this.downloadurl.equals(other.getDownloadurl()))) &&
            ((this.imagefileid==null && other.getImagefileid()==null) || 
             (this.imagefileid!=null &&
              this.imagefileid.equals(other.getImagefileid()))) &&
            ((this.imagefilename==null && other.getImagefilename()==null) || 
             (this.imagefilename!=null &&
              this.imagefilename.equals(other.getImagefilename()))) &&
            ((this.imagefilesize==null && other.getImagefilesize()==null) || 
             (this.imagefilesize!=null &&
              this.imagefilesize.equals(other.getImagefilesize())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getDownloadurl() != null) {
            _hashCode += getDownloadurl().hashCode();
        }
        if (getImagefileid() != null) {
            _hashCode += getImagefileid().hashCode();
        }
        if (getImagefilename() != null) {
            _hashCode += getImagefilename().hashCode();
        }
        if (getImagefilesize() != null) {
            _hashCode += getImagefilesize().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FileInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservices.docs.weaver", "FileInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("downloadurl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservices.docs.weaver", "downloadurl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imagefileid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservices.docs.weaver", "imagefileid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imagefilename");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservices.docs.weaver", "imagefilename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imagefilesize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservices.docs.weaver", "imagefilesize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
