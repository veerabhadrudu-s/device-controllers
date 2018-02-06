/*
 * Author: sveera
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { error } from 'selenium-webdriver';
import { PluginScriptService } from '../../services/pluginScript.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload-plugin',
  templateUrl: './upload-plugin.component.html'
})
export class UploadPluginComponent implements OnInit, OnDestroy {

  bannerImage: String = './assets/images/service.jpg';
  scriptFileToBeUploaded: File = null;
  scriptFileNameUploaded: String = null;

  constructor(private pluginScriptService: PluginScriptService, private router: Router) { }

  ngOnInit() {
    console.log(this.constructor.name + ' Instance initialized');
  }

  ngOnDestroy(): void {
    console.log(this.constructor.name + ' Instance destroyed ');
  }

  updateChangedFileName(event): void {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      const selectedFileName: File = fileList[0];
      const fileNameParts: String[] = selectedFileName.name.split('.');
      console.log(' File Parts ' + fileNameParts);
      if (this.isValidFileName(fileNameParts)) {
        this.updateSelectedFileName(selectedFileName);
      } else {
        this.scriptFileToBeUploaded = null;
      }
    }
  }

  private updateSelectedFileName(selectedFileName: File) {
    this.scriptFileToBeUploaded = selectedFileName;
    console.log('Selected plugin script file name ' + this.scriptFileToBeUploaded.name +
      ' with size ' + this.scriptFileToBeUploaded.size + ' was last modified at ' +
      this.scriptFileToBeUploaded.lastModifiedDate + ' is of file type ' + this.scriptFileToBeUploaded.type);
  }

  private isValidFileName(fileNameParts: String[]): boolean {
    return fileNameParts.length > 1 && fileNameParts[fileNameParts.length - 1] === 'groovy';
  }

  uploadScriptPlugin(): void {
    console.log('scriptFileUploadPath value is' + this.scriptFileToBeUploaded);
    const scriptFileToBeUploaded: File = this.scriptFileToBeUploaded;
    this.scriptFileToBeUploaded = null;
    const observable: Observable<Response> = this.pluginScriptService.uploadPluginScript(scriptFileToBeUploaded);
    this.scriptFileNameUploaded = scriptFileToBeUploaded.name;
    this.handleResponseAsyncronously(observable);
  }


  private handleResponseAsyncronously(observable: Observable<Response>) {
    observable.subscribe((responseMessage: Response) => {
      console.log('HTTP Status code from response for Plugin Script upload service ' + responseMessage.status);
      console.log('HTTP Body text is ' + responseMessage.json());
      console.log('HTTP Response status for Plugin script upload service ' + responseMessage.json().status);
      this.router.navigate(['/pluginScriptDeploymentStatus/' + this.scriptFileNameUploaded]);
    }, (err: any) => {
      console.error('Failed to upload Plugin script');
      console.error(err);
    }, () => {
      console.log('Completed the POST REST request for Script Upload');
      this.scriptFileNameUploaded = null;
    });
  }
}
