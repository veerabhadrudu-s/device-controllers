/*
 * Author: sveera
 */

import { FooterComponent } from './footer.component';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BaseHrefProviderService } from '../../services/base.href.service';

fdescribe('Test cases related to ' + FooterComponent.name, () => {

    let footerComponentFixture: ComponentFixture<FooterComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [FooterComponent],
            providers: [BaseHrefProviderService]
        });
        footerComponentFixture = TestBed.createComponent(FooterComponent);
    });

    fit('Is ' + FooterComponent.name + ' not null', () => {
        const footerComponent: FooterComponent = footerComponentFixture.componentInstance;
        footerComponentFixture.whenStable().then(() => {
            footerComponentFixture.detectChanges();
            expect(footerComponent).not.toBeNull();
        });
    });

});
