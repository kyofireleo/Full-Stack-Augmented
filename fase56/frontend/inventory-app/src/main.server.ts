import { BootstrapContext, bootstrapApplication } from '@angular/platform-browser';
import { InventarioApp } from './app/app';
import { config } from './app/app.config.server';

const bootstrap = (context: BootstrapContext) =>
    bootstrapApplication(InventarioApp, config, context);

export default bootstrap;
