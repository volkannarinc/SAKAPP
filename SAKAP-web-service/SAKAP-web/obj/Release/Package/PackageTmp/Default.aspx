<%@ Page Title="Home Page" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true" CodeBehind="Default.aspx.cs" Inherits="SAKAP_web._Default" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <%
        List<SAKAP_web.PKG.Dto> list = GetData();

            foreach (SAKAP_web.PKG.Dto item in list)
            {
                Response.Write("(name=");
                Response.Write(item.name);
                Response.Write(",");
                Response.Write("lat=");
                Response.Write(item.x);
                Response.Write(",");
                Response.Write("lon=");
                Response.Write(item.y);
                Response.Write(",");
                Response.Write("angle=");
                Response.Write(item.angle);
                Response.Write(")");
            }

           
    
         %>
</asp:Content>
