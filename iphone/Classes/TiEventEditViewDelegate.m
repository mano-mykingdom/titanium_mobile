//
//  TiEventEditViewDelegate.m
//  Test
//
//  Created by Manojkumar on 1/10/17.
//
//

#import "TiEventEditViewDelegate.h"
#import "TiApp.h"

@implementation TiEventEditViewDelegate

#pragma mark - Internals

-(id)initWithCallback:(KrollCallback*)callback_
{
    if (self = [super init]) {
        callback = [callback_ retain];
    }
    return self;
}

#pragma mark - EKEventEditViewDelegate

- (void)eventEditViewController:(EKEventEditViewController *)controller
          didCompleteWithAction:(EKEventEditViewAction)action
{
    if(closed != YES) {
        
        closed = YES;
        
        TiThreadPerformOnMainThread(^{
            [[TiApp app] hideModalController:controller
                                    animated:YES];
            
        }, YES);
        
        NSDictionary * propertiesDict = @{
                                          @"cancel":
                                              NUMINT(action == EKEventEditViewActionCanceled),
                                          @"save":
                                              NUMINT(action == EKEventEditViewActionSaved),
                                          @"delete":
                                              NUMINT(action == EKEventEditViewActionDeleted)
                                          };
        NSArray * invocationArray = [[NSArray alloc] initWithObjects:&propertiesDict count:1];
        
        [callback call:invocationArray thisObject:nil];
        [invocationArray release];
    }
}

@end
