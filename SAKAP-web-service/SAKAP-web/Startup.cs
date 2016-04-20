using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(SAKAP_web.Startup))]
namespace SAKAP_web
{
    public partial class Startup {
        public void Configuration(IAppBuilder app) {
            ConfigureAuth(app);
        }
    }
}
